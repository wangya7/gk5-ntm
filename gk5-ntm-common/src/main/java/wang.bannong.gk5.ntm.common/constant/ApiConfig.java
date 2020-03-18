package wang.bannong.gk5.ntm.common.constant;

/**
 * 常量池
 * <p>
 * Created by bn. on 2019/10/11 2:15 PM
 */
public class ApiConfig {

    /**
     * 名称
     */
    public static final String API = "api";

    /**
     * 认证 token信息
     */
    public static final String IA = "ia";

    /**
     * 认证 token信息（旧）
     */
    public static final String OIA = "oia";

    /**
     * 版本
     */
    public static final String V = "v";

    /**
     * 时间戳
     */
    public static final String TS = "ts";

    /**
     * 经纬度
     */
    public static final String LNG = "lng";
    public static final String LAT = "lat";

    /**
     * 客户端软件、硬件信息
     */
    public static final String TTID = "ttid";

    /**
     * 业务系统标识 autoloan,chech
     */
    public static final String APPID = "appid";

    /**
     * 请求参数
     */
    public static final String DATA = "data";

    /**
     * 渠道 从属appid下
     */
    public static final String CHANNEL = "channel";

    /**
     * 签名
     */
    public static final String SIGN = "sign";

    /**
     * 客户端签名规则
     */
    public static final String SIGN_FORMAT = "api=%s&appid=%s&channel=%s&data=%s&ia=%s&ts=%s&ttid=%s&v=%s";

    /**
     * IP
     */
    public static final String IP        = "ip";
    public static final String CODE      = "code";
    public static final String SUCCESS   = "success";
    public static final String MSG       = "msg";
    public static final String CREATE_MS = "createMs";
    public static final String API_ID    = "apiId";
    public static final String ID        = "id";
    public static final String MOBILE    = "mobile";
    public static final String NAME      = "name";
    public static final String ICON      = "icon";


    /** 特殊的API名称定义 */
    public static final String LOGIN_API     = "login";
    public static final String LOGOUT_API    = "logout";
    public static final String LOGIN_API_H5  = LOGIN_API + ".h5";
    public static final String LOGOUT_API_H5 = LOGOUT_API + ".h5";


}
