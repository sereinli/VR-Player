package me.rain.android.vrvideo.imageselector;

public class Constant {
	public static final String EXTRA_PKIMAGE_OBJ = "pkimage_object";
	
	//可选最大图片数
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    //选图模式：单选/多选
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    //是否显示“拍照”
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    //选中的图片项
    public static final String EXTRA_RESULT = "select_result";
    //默认选中的图片项
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    //图片裁剪比例
    public static final String EXTRA_RATIO = "cropper_ratio";
    //默认分组（红、蓝、无）用于区分界面显示颜色配置
    public static final String EXTRA_DEFAULT_GROUP = "default_group";
    //是否循环模式(单图选择+裁切  PK话题红蓝队循环选择)
    public static final String EXTRA_CYCLE_MODE = "cycle_mode";

    public static final int MODE_SINGLE = 0;
    public static final int MODE_MULTI = 1;
    
    public static final int NO_GROUP = 0;
    public static final int GROUP_RED = 1;
    public static final int GROUP_BLUE = 2;
    
    public static final int CYCLE_MODE_SINGLE = 0;
    public static final int CYCLE_MODE_PK = 1;
    
    public static final int RATIO_1V1 = 1;
    public static final int RATIO_2V3 = 2;
    public static final int RATIO_3V4 = 3;
    
    public static String IMAGE_SELECTOR_CLOSE_RED = "com.creativemind.star.closeredgroup";
    public static String IMAGE_SELECTOR_CLOSE_BLUE = "com.creativemind.star.closebluegroup";
}
