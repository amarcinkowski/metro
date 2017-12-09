package com.github.amarcinkowski.metro;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by amarcinkowski on 11.03.17.
 */
@Getter
@ToString
public class Params {

    private Vector<String> params = new Vector<>();
    private HashMap<String,String> paramTypes = new HashMap<>();

    private void addParam(String param) {
        this.params.add(param);
    }

    private void addParamType(String type, String param) {
        this.paramTypes.put(type,param);
    }

    public void add(MetroParser.CommandParameterContext ctx) {
        // if has type set add typed
        String param = ctx.ID().getText();
        if (ctx.type() != null) {
            String type = ctx.type().getText();
            addParamType(type,param);
        }
        addParam(param);
    }

    public String get(Integer index) {
        return params.get(index);
    }

    public void replace(Integer currentParamIndex, String value) {
        String paramName = params.elementAt(currentParamIndex);
        params.removeElementAt(currentParamIndex);
        params.add(currentParamIndex, value);
        // replace
        paramTypes.forEach( (k,v) -> {if (v.equals(paramName)) {paramTypes.put(k, value);} });
    }

    public void add(int index, String value) {
        params.add(index,value);
    }

}
