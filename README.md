# MasterViewModelRetrofitRxJava

## 介绍
利用 ViewModel，LiveData，Retrofit和RxJava封装的网络请求框架。

## 前言
使用ViewModel + LiveData + Retrofit + RxJava构建一个网络请求的框架。

目前包含以下功能：
- 网络请求结果基于观察者模式进行传递，回调函数与UI层的声明周期绑定，避免内存泄漏。
- 数据加载时的`startLoading()`与加载结束后的`dismissLoading()`操作都是自动调用的，具体实现封装在基类中。子类也可以实现自己的特定实现。
- 当网络请求结果为非成功状态时（网络请求失败或业务请求失败），默认操作是用Toast提示失败的原因，支持自定义实现失败的操作。
- 逻辑操作与UI层分离，基于观察者模式来实现消息驱动UI变化。提供了在ViewModel中操作UI变化的能力，包括使Activity/Fragment弹出对话框、Toast消息、finishActivity等UI操作，但ViewModel不持有Activity/Fragment的引用，而是基于消息驱动实现，从而避免内存泄漏。

源码地址：[MasterViewModelRetrofitRxJava](https://gitee.com/QingFengBaiYu/MasterViewModelRetrofitRxJava)

## 封装BaseViewModel与BaseActivity
ViewModel与LiveData都是Android Jetpack架构组件之一。ViewModel被设计用来存储和管理UI相关数据，以便数据能在界面销毁时（如屏幕旋转）保存数据，而与ViewModel挂钩的LiveData时一个用于保存可以被观察的值的数据持有类，且遵循应用组件的生命周期，只有在组件的生命周期处于活跃状态时，才会收到数据更新的通知。

既然是消息驱动，那么就需要一个用于抽象消息类型的Event类。
```java
public class BaseEvent {
    private int mAction;

    public BaseEvent(int action) {
        mAction = action;
    }

    public int getAction() {
        return mAction;
    }
}

public class BaseActionEvent extends BaseEvent {

    public static final int SHOW_LOADING_DIALOG = 1;

    public static final int DISMISS_LOADING_DIALOG = 2;

    public static final int SHOW_TOAST = 3;

    public static final int FINISH = 4;

    public static final int FINISH_WITH_RESULT_OK = 5;

    private String mMessage;

    public BaseActionEvent(int action) {
        super(action);
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
```
`BaseActionEvent`即用于向View层传递Action的Model，在ViewModel通过向View层传递不同的消息类型，从而触发相对应的操作。因此，`BaseViewModel`需要向子类提供默认的实现。
```java
public interface IViewModelAction {

    void startLoading();

    void startLoading(String message);

    void dismissLoading();

    void showToast(String message);

    void finish();

    void finishWithResultOk();

    MutableLiveData<BaseActionEvent> getActionLiveData();

}

public class BaseViewModel extends ViewModel implements IViewModelAction {

    private MutableLiveData<BaseActionEvent> mActionLiveData;

    protected LifecycleOwner mLifecycleOwner;

    public BaseViewModel() {
        mActionLiveData = new MutableLiveData<>();
    }

    @Override
    public void startLoading() {
        startLoading(null);
    }

    @Override
    public void startLoading(String message) {
        BaseActionEvent baseActionEvent = new BaseActionEvent(BaseActionEvent.SHOW_LOADING_DIALOG);
        baseActionEvent.setMessage(message);
        mActionLiveData.setValue(baseActionEvent);
    }

    @Override
    public void dismissLoading() {
        mActionLiveData.setValue(new BaseActionEvent(BaseActionEvent.DISMISS_LOADING_DIALOG));
    }

    @Override
    public void showToast(String message) {
        BaseActionEvent baseActionEvent = new BaseActionEvent(BaseActionEvent.SHOW_TOAST);
        baseActionEvent.setMessage(message);
        mActionLiveData.setValue(baseActionEvent);
    }

    @Override
    public void finish() {
        mActionLiveData.setValue(new BaseActionEvent(BaseActionEvent.FINISH));
    }

    @Override
    public void finishWithResultOk() {
        mActionLiveData.setValue(new BaseActionEvent(BaseActionEvent.FINISH_WITH_RESULT_OK));
    }

    @Override
    public MutableLiveData<BaseActionEvent> getActionLiveData() {
        return mActionLiveData;
    }

    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
    }
}
```
作为消息发送方的`BaseViewModel`的具体实现就完成了，之后是消息的接收方`BaseActivity/BaseFragment`。

`BaseActivity`通过监听`BaseViewModel`中`mActionLiveData`的数据变化，从而在网络请求开始加载时`startLoading()`，在加载结束时`dismissLoading()`。

一般一个Activity对应一个ViewModel，少部分情况时会对应多个ViewModel，因此`initViewModel()`声明为抽象方法，而`initViewModelList()`默认返回null。
## 封装Retrofit与RxJava
框架实现了请求失败的操作（Toast提示失败原因），也支持自定义回调接口。因此，需要两个回调接口，一个只包含请求成功时的回调接口，另一个只包含了一个请求失败时的回调接口。
```java
public interface RequestCallback<T> {
    void onSuccess(T t);
}

public interface RequestMultiplyCallback<T> extends RequestCallback<T> {
    void onFail(BaseException e);
}
```
此外，为了在网络请求成功但业务逻辑失败时（例如，请求参数缺失、Token失效等），可以抛出详细的失败信息，需要自定义`BaseException`。
```java
public class BaseException extends RuntimeException {
    private int mErrorCode = HttpCode.CODE_UNKNOWN;

    public BaseException() {
    }

    public BaseException(int errorCode, String message) {
        super(message);
        mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }
}
```
实现具体的异常类
```java
public class ParameterInvalidException extends BaseException {
    public ParameterInvalidException() {
        super(HttpCode.CODE_PARAMETER_INVALID, "参数有误");
    }
}

public class TokenInvalidException extends BaseException {
    public TokenInvalidException() {
        super(HttpCode.CODE_TOKEN_INVALID, "Token失效");
    }
}
```
为了提升性能，Retrofit一般设计成单例模式。为了应对应用中`BaseUrl`可能多个的情况（Demo就是如此），此外使用Map来存储多个Retrofit实例。
```java
public class RetrofitManagement {
    private static final long READ_TIMEOUT = 6000;

    private static final long WRITE_TIMEOUT = 6000;

    private static final long CONNECT_TIMEOUT = 6000;

    private final Map<String, Object> mServiceMap = new ConcurrentHashMap<>();

    private RetrofitManagement() {
    }

    public static RetrofitManagement getInstance() {
        return RetrofitHolder.mRetrofitManagement;
    }

    private static class RetrofitHolder {
        private static final RetrofitManagement mRetrofitManagement = new RetrofitManagement();
    }

    private Retrofit createRetrofit(String url) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new FilterInterceptor())
                .retryOnConnectionFailure(true);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
            builder.addInterceptor(new ChuckInterceptor(ContextHolder.getContext()));
        }
        OkHttpClient client = builder.build();
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    <T> ObservableTransformer<BaseResponseBody<T>, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(result -> {
                    switch (result.getCode()) {
                        case HttpCode.CODE_SUCCESS:
                            return createData(result.getData());
                        case HttpCode.CODE_TOKEN_INVALID: {
                            throw new TokenInvalidException();
                        }
                        case HttpCode.CODE_ACCOUNT_INVALID: {
                            throw new AccountInvalidException();
                        }
                        default: {
                            throw new ServerResultException(result.getCode(), result.getMsg());
                        }
                    }
                });
    }

    private <T> ObservableSource<? extends T> createData(T t) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(t);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    <T> T getService(Class<T> clazz) {
        return getService(clazz, HttpConfig.BASE_URL_WEATHER);
    }

    @SuppressWarnings("unchecked")
    <T> T getService(Class<T> clazz, String host) {
        T value;
        if (mServiceMap.containsKey(host)) {
            Object obj = mServiceMap.get(host);
            if (obj == null) {
                value = createRetrofit(host).create(clazz);
                mServiceMap.put(host, value);
            } else {
                value = (T) obj;
            }
        } else {
            value = createRetrofit(host).create(clazz);
            mServiceMap.put(host, value);
        }
        return value;
    }
}
```
此外还需要自定义一个Observer来对数据请求结果进行自定义回调。
```java
public class BaseSubscriber<T> extends DisposableObserver<T> {

    private BaseViewModel mBaseViewModel;

    private RequestCallback<T> mRequestCallback;

    public BaseSubscriber(BaseViewModel baseViewModel) {
        mBaseViewModel = baseViewModel;
    }

    public BaseSubscriber(BaseViewModel baseViewModel, RequestCallback<T> requestCallback) {
        mBaseViewModel = baseViewModel;
        mRequestCallback = requestCallback;
    }

    @Override
    public void onNext(T t) {
        if (mRequestCallback != null) {
            mRequestCallback.onSuccess(t);
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (mRequestCallback instanceof RequestMultiplyCallback) {
            RequestMultiplyCallback callback = (RequestMultiplyCallback) mRequestCallback;
            if (e instanceof BaseException) {
                callback.onFail((BaseException) e);
            } else {
                callback.onFail(new BaseException(HttpCode.CODE_UNKNOWN, e.getMessage()));
            }
        } else {
            if (mBaseViewModel == null) {
                Toast.makeText(ContextHolder.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                mBaseViewModel.showToast(e.getMessage());
            }
        }
    }

    @Override
    public void onComplete() {

    }
}
```
## BaseRemoteDataSource与BaseRepo
上面介绍的`RequestCallback`、`RetrofitManagement`与`BaseSubscriber`还是一个个单独的个体，还需要一个链接器串起来，这个链接器的实现类就是`BaseRemoteDataSource`。在这里，对`BaseRemoteDataSource`的定位是将之当成一个接口实现者，即在`RemoteDataSource`中实际调用各个请求接口，并通过RxJava来控制loading弹出以及销毁的时机。一般而言，`BaseRemoteDataSource`的实现类中声明的是具有相关逻辑的接口。例如，对于登录模块，可声明一个`LoginDataSource`，对于设置模块，可以声明一个`SettingDataSource`。
```java
public abstract class BaseRemoteDataSource {
    private CompositeDisposable mCompositeDisposable;
    private BaseViewModel mBaseViewModel;

    public BaseRemoteDataSource(BaseViewModel baseViewModel) {
        mCompositeDisposable = new CompositeDisposable();
        mBaseViewModel = baseViewModel;
    }

    protected <T> T getService(Class<T> clazz) {
        return RetrofitManagement.getInstance().getService(clazz);
    }

    protected <T> T getService(Class<T> clazz, String host) {
        return RetrofitManagement.getInstance().getService(clazz, host);
    }

    private <T> ObservableTransformer<BaseResponseBody<T>, T> applySchedulers() {
        return RetrofitManagement.getInstance().applySchedulers();
    }

    protected <T> void execute(Observable observable, RequestCallback<T> callback) {
        execute(observable, new BaseSubscriber<>(mBaseViewModel, callback), true);
    }

    protected <T> void execute(Observable observable, RequestMultiplyCallback<T> callback) {
        execute(observable, new BaseSubscriber<>(mBaseViewModel, callback), true);
    }

    public void executeWithoutDismiss(Observable observable, Observer observer) {
        execute(observable, observer, false);
    }

    @SuppressWarnings("unchecked")
    private void execute(Observable observable, Observer observer, boolean isDismiss) {
        Disposable disposable = (Disposable) observable
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(applySchedulers())
                .compose(isDismiss ? loadingTransformer() : loadingTransformerWithoutDismiss())
                .subscribeWith(observer);
        addDisposable(disposable);
    }

    private <T> ObservableTransformer<T, T> loadingTransformer() {
        return observable -> observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> startLoading())
                .doFinally(this::dismissLoading);
    }

    private ObservableTransformer loadingTransformerWithoutDismiss() {
        return observable -> observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> startLoading());
    }

    private void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    public void dispose() {
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
    }

    private void startLoading() {
        if (mBaseViewModel != null) {
            mBaseViewModel.startLoading();
        }
    }

    private void dismissLoading() {
        if (mBaseViewModel != null) {
            mBaseViewModel.dismissLoading();
        }
    }
}
```
除了`BaseRemoteDataSource`之外，还需要一个`BaseRepo`。对`BaseRepo`的定位是将其当作一个接口调度器，其持有`BaseRemoteDataSource`的实例并中转ViewModel的接口调用请求，并可以在`BaseRepo`分担一部分数据处理逻辑。
```java
public class BaseRepo<T> {
    protected T mRemoteDataSource;

    public BaseRepo(T remoteDataSource) {
        mRemoteDataSource = remoteDataSource;
    }
}
```
这样，ViewModel不关心接口的实际调用实现，方便以后更换`BaseRemoteDataSource`的实现方式，且将一部分的数据处理逻辑放到了`BaseRepo`，有利于逻辑的复用。
## 实践操作
#### 请求天气数据
首先声明接口。
```java
public interface ApiService {
    @Headers({HttpConfig.HTTP_REQUEST_TYPE_KEY + ":" + HttpConfig.HTTP_REQUEST_WEATHER})
    @GET("onebox/weather/query")
    Observable<BaseResponseBody<Weather>> queryWeather(@Query("cityname") String cityName);
}
```
增加的头部信息是为了标明该接口的请求类型，因为本文的demo的几个接口所用到的`baseUrl`以及请求`key`并不相同，因此通过声明头部来为接口动态指定请求参数，这就需要用到Retrofit拦截器。
```java
public class FilterInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl.Builder httpBuilder = originalRequest.url().newBuilder();
        Headers headers = originalRequest.headers();
        if (headers.size() > 0) {
            String requestType = headers.get(HttpConfig.HTTP_REQUEST_TYPE_KEY);
            if (!TextUtils.isEmpty(requestType)) {
                assert requestType != null;
                switch (requestType) {
                    case HttpConfig.BASE_URL_WEATHER:
                        httpBuilder.addQueryParameter(HttpConfig.KEY, HttpConfig.KEY_WEATHER);
                        break;
                    case HttpConfig.BASE_URL_QR_CODE:
                        httpBuilder.addQueryParameter(HttpConfig.KEY, HttpConfig.KEY_QR_CODE);
                        break;
                    case HttpConfig.BASE_URL_NEWS:
                        httpBuilder.addQueryParameter(HttpConfig.KEY, HttpConfig.KEY_NEWS);
                        break;
                }
            }
        }
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .removeHeader(HttpConfig.HTTP_REQUEST_TYPE_KEY)
                .url(httpBuilder.build());
        return chain.proceed(requestBuilder.build());
    }
}
```
声明`BaseRemoteDataSource`的实现类`WeatherDataSource`。
```java
public class WeatherDataSource extends BaseRemoteDataSource implements IWeatherDataSource {
    public WeatherDataSource(BaseViewModel baseViewModel) {
        super(baseViewModel);
    }

    @Override
    public void queryWeather(String cityName, RequestCallback<Weather> callback) {
        execute(getService(ApiService.class).queryWeather(cityName), callback);
    }
}
```
声明`BaseRepo`的实现类`WeatherRepo`。
```java
public class WeatherRepo extends BaseRepo<IWeatherDataSource> {
    public WeatherRepo(IWeatherDataSource remoteDataSource) {
        super(remoteDataSource);
    }

    public MutableLiveData<Weather> queryWeather(String cityName) {
        MutableLiveData<Weather> weatherMutableLiveData = new MutableLiveData<>();
        mRemoteDataSource.queryWeather(cityName, weatherMutableLiveData::setValue);
        return weatherMutableLiveData;
    }
}
```
还需要一个`WeatherViewModel`，View层通过调用`queryWeather()`方法在请求成功时触发`weatherLiveData`更新数据，View层已事先监听`weatherLiveData`，并在数据更新时就可以立即收到最新数据。
```java
public class WeatherViewModel extends BaseViewModel {

    private MutableLiveData<Weather> mWeatherLiveData;

    private WeatherRepo mWeatherRepo;

    public WeatherViewModel() {
        mWeatherLiveData = new MutableLiveData<>();
        mWeatherRepo = new WeatherRepo(new WeatherDataSource(this));
    }

    public void queryWeather(String cityName) {
        mWeatherRepo.queryWeather(cityName).observe(mLifecycleOwner, weather ->
                mWeatherLiveData.setValue(weather));
    }

    public MutableLiveData<Weather> getWeatherLiveData() {
        return mWeatherLiveData;
    }
}
```
在`QueryWeatherActivity`中打印出请求结果。
```java
public class QueryWeatherActivity extends BaseActivity {

    private WeatherViewModel mWeatherViewModel;
    private EditText mEtCityName;
    private TextView mTvWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_weather);
        mEtCityName = findViewById(R.id.et_cityName);
        mTvWeather = findViewById(R.id.tv_weather);
    }

    @Override
    protected ViewModel initViewModel() {
        mWeatherViewModel = LViewModelProviders.of(this, WeatherViewModel.class);
        mWeatherViewModel.getWeatherLiveData().observe(this, this::handlerWeather);
        return null;
    }

    private void handlerWeather(Weather weather) {
        StringBuilder result = new StringBuilder();
        for (Weather.InnerWeather.NearestWeather nearestWeather : weather.getData().getWeather()) {
            result.append("\n\n").append(new Gson().toJson(nearestWeather));
        }
        mTvWeather.setText(result.toString());
    }

    public void queryWeather(View view) {
        mTvWeather.setText(null);
        mWeatherViewModel.queryWeather(mEtCityName.getText().toString());
    }
}
```
请求一个接口需要建立三个实现类（`WeatherDataSource`、`WeatherRepo`、`WeatherViewModel`）以及一个接口（`IWeatherDataSource`），这是要划分指责并实现逻辑与UI分离的必然结果。
- `WeatherDataSource`用来实现接口的实际调用，只负责请求数据并传递请求结果。
- `WeatherRepo`用来屏蔽`WeatherViewModel`对`WeatherDataSource`的感知，并承担一部分数据处理逻辑。
- `WeatherViewModel`用于实现逻辑与UI的隔离，并保障数据不因为也买你重建而丢失。

这样，Activity就可以尽量只承担数据呈现的职责，而必须参与数据处理逻辑。
#### 请求生成二维码
生成指定内容的二维码。
```java
public class QrCodeDataSource extends BaseRemoteDataSource implements IQrCodeDataSource {
    public QrCodeDataSource(BaseViewModel baseViewModel) {
        super(baseViewModel);
    }

    @Override
    public void createQrCode(String text, int width, RequestCallback<QrCode> callback) {
        execute(getService(ApiService.class, HttpConfig.BASE_URL_QR_CODE).createQrCode(text, width), callback);
    }
}
```
此处接口请求返回的只是一段base64编码的字符串，而外部希望得到的是一个可以直接使用的Bitmap，因此可以在Repo中先对数据进行转换后再传递到外部。
```java
public class QrCodeRepo extends BaseRepo<IQrCodeDataSource> {
    public QrCodeRepo(IQrCodeDataSource remoteDataSource) {
        super(remoteDataSource);
    }

    public MutableLiveData<QrCode> createQrCode(String text, int width) {
        MutableLiveData<QrCode> liveData = new MutableLiveData<>();
        mRemoteDataSource.createQrCode(text, width, qrCode -> Observable
                .create((ObservableOnSubscribe<Bitmap>) emitter -> {
                    Bitmap bitmap = base64ToBitmap(qrCode.getBase64_image());
                    emitter.onNext(bitmap);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    qrCode.setBitmap(bitmap);
                    liveData.setValue(qrCode);
                }));
        return liveData;
    }

    private static Bitmap base64ToBitmap(String base64String) {
        byte[] decode = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decode, 0, decode.length);
    }
}

public class QrCodeViewModel extends BaseViewModel {
    private MutableLiveData<QrCode> mQrCodeMutableLiveData = new MutableLiveData<>();

    private QrCodeRepo mQrCodeRepo = new QrCodeRepo(new QrCodeDataSource(this));

    public void createQrCode(String text, int width) {
        mQrCodeRepo.createQrCode(text, width).observe(mLifecycleOwner, qrCode ->
                mQrCodeMutableLiveData.setValue(qrCode));
    }

    public MutableLiveData<QrCode> getQrCodeMutableLiveData() {
        return mQrCodeMutableLiveData;
    }
}
```
#### 请求失败示例
本文封装的网络框架当网络请求结果为非成功状态时（网络请求失败或业务逻辑请求失败），默认操作是用Toast提示失败的原因，也支持自定义实现失败时的操作。

声明两个并不存在的接口。
```java
public interface ApiService {

    @GET("leavesC/test1")
    Observable<BaseResponseBody<String>> test1();

    @GET("leavesC/test2")
    Observable<BaseResponseBody<String>> test2();

}
public class FailExampleDataSource extends BaseRemoteDataSource implements IFailExampleDataSource {

    public FailExampleDataSource(BaseViewModel baseViewModel) {
        super(baseViewModel);
    }

    @Override
    public void test1(RequestCallback<String> callback) {
        execute(getService(ApiService.class).test1(), callback);
    }

    @Override
    public void test2(RequestCallback<String> callback) {
        execute(getService(ApiService.class).test2(), callback);
    }

}
```
`test1()`用的时基础类的默认失败回调，即直接Toast提示失败信息。而`test2()`则是自定义请求失败的回调操作。