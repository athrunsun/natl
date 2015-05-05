package net.nitrogen.ates.dashboard.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import net.nitrogen.ates.core.model.custom_parameter.CustomParameterModel;
import net.nitrogen.ates.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class RawCustomParameterHandlingInterceptor implements Interceptor {
    public static final String RAW_CUSTOM_PARAMETER_MAP_ATTR_NAME = "rawCustomParameterMap";

    @Override
    public void intercept(ActionInvocation ai) {
        String[] customFieldName = ai.getController().getParaValues("customFieldName");
        String[] customFieldValue = ai.getController().getParaValues("customFieldValue");
        String[] customFieldType = ai.getController().getParaValues("customFieldType");
        Map<String, CustomParameterModel> rawCustomParameterMap = new HashMap<>();

        for(int i = 0; customFieldName != null && i < customFieldName.length; i++) {
            if(!StringUtil.isNullOrWhiteSpace(customFieldName[i])) {
                CustomParameterModel parameter = new CustomParameterModel();
                parameter.setKey(customFieldName[i]);
                parameter.setValue(customFieldValue[i]);
                parameter.setType(Integer.parseInt(customFieldType[i]));
                rawCustomParameterMap.put(customFieldName[i], parameter);
            }
        }

        ai.getController().setAttr(RAW_CUSTOM_PARAMETER_MAP_ATTR_NAME, rawCustomParameterMap);
        ai.invoke();
    }
}
