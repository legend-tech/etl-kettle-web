/**
 * Copyright (C), 2019-2020, **有限公司
 * FileName: GetVariable
 * Author:   zhuzj29042
 * Date:     2020/2/14 17:18::49
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.caixin.data.middle.etl.kettle.admin.trans.ext.steps;

import com.caixin.data.middle.etl.kettle.admin.trans.ext.step.AbstractStep;
import com.caixin.data.middle.etl.kettle.ext.core.PropsUI;
import com.caixin.data.middle.etl.kettle.ext.util.JSONArray;
import com.caixin.data.middle.etl.kettle.ext.util.JSONObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.getvariable.GetVariableMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 *
 *
 * @author zhuzhongji
 * @create 2020/2/14 17:18:49
 * @since 1.0.0
 */
@Component("GetVariable")
@Scope("prototype")
public class GetVariable extends AbstractStep {

    @Override
    public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
        GetVariableMeta getVariableMeta = (GetVariableMeta) stepMetaInterface;

        String fields = cell.getAttribute("fields");
        JSONArray jsonArray = JSONArray.fromObject(fields);
        GetVariableMeta.FieldDefinition[] fieldDefinitions =new GetVariableMeta.FieldDefinition[jsonArray.size()];

        for(int i=0; i<jsonArray.size(); i++) {
            GetVariableMeta.FieldDefinition var1=new GetVariableMeta.FieldDefinition();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            var1.setFieldName(jsonObject.optString("name"));
            var1.setVariableString(jsonObject.optString("variable"));
            var1.setFieldType(ValueMetaFactory.getIdForValueMeta(jsonObject.optString("type")));
            var1.setFieldFormat(jsonObject.optString("format"));
            var1.setCurrency(jsonObject.optString("currency"));
            var1.setDecimal(jsonObject.optString("decimal"));
            var1.setGroup(jsonObject.optString("group"));
            var1.setFieldLength(Const.toInt(jsonObject.optString("length"), -1));
            var1.setFieldPrecision(Const.toInt(jsonObject.optString("precision"), -1));
            var1.setTrimType(ValueMetaString.getTrimTypeByCode(jsonObject.optString("trim_type")));
            if(var1.getFieldType() == 0) {
                var1.setFieldType(2);
            }
            fieldDefinitions[i]=var1;
        }
        getVariableMeta.setFieldDefinitions(fieldDefinitions);
    }

    @Override
    public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
        GetVariableMeta getVariableMeta = (GetVariableMeta) stepMetaInterface;
        Document doc = mxUtils.createDocument();
        Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

        JSONArray jsonArray = new JSONArray();
        GetVariableMeta.FieldDefinition[] fieldDefinitions = getVariableMeta.getFieldDefinitions();
        if(null!=fieldDefinitions) {
            for(int i=0; i<fieldDefinitions.length; i++) {
                String fieldName=fieldDefinitions[i].getFieldName();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", fieldName);
                jsonObject.put("variable", fieldDefinitions[i].getVariableString());
                jsonObject.put("type", fieldDefinitions[i].getFieldType());
                jsonObject.put("format", fieldDefinitions[i].getFieldFormat());
                jsonObject.put("currency", fieldDefinitions[i].getCurrency());
                jsonObject.put("decimal", fieldDefinitions[i].getDecimal());
                jsonObject.put("group", fieldDefinitions[i].getGroup());
                jsonObject.put("length", fieldDefinitions[i].getFieldLength());
                jsonObject.put("precision", fieldDefinitions[i].getFieldPrecision());
                jsonObject.put("trim_type", ValueMetaString.getTrimTypeCode(fieldDefinitions[i].getTrimType()));
                jsonArray.add(jsonObject);
            }
        }
        e.setAttribute("fields", jsonArray.toString());

        return e;
    }

}
