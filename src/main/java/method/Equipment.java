package method;

import java.util.Objects;

public class Equipment {
    private String code;
    private String model;
    private String state;

    public Equipment(String code, String model, String state) {
        setCode(code);
        setModel(model);
        setState(state);
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if(code==null || code.isBlank())
            throw new IllegalArgumentException("编号不能为空");
        this.code = code;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        if(model==null||model.isBlank())
            throw new IllegalArgumentException("模型不能为空");
        this.model = model;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if(state==null||state.isBlank())
            throw new IllegalArgumentException("状态不能为空");
        this.state = state;
    }

    @Override
    public String toString() {
        return code+","+model+","+state;
    }

    public void archive(){
        if(Objects.equals(this.state, "ACTIVE"))
        {
            setState("ARCHIVED");
        }else if(Objects.equals(this.state,"ARCHIVED"))
        {
            throw new IllegalStateException("设备已归档，不能重复归档");
        }else{
            throw new IllegalStateException("设备未激活");
        }

    }

}
