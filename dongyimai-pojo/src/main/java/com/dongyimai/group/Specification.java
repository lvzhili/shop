package com.dongyimai.group;

import com.dongyimai.pojo.TbSpecification;
import com.dongyimai.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

public class Specification implements Serializable {

    private TbSpecification tbSpecification;
    private List<TbSpecificationOption> optionList;

    public Specification() {
    }

    public Specification(TbSpecification tbSpecification, List<TbSpecificationOption> optionList) {
        this.tbSpecification = tbSpecification;
        this.optionList = optionList;
    }

    public TbSpecification getTbSpecification() {
        return tbSpecification;
    }

    public void setTbSpecification(TbSpecification tbSpecification) {
        this.tbSpecification = tbSpecification;
    }

    public List<TbSpecificationOption> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<TbSpecificationOption> optionList) {
        this.optionList = optionList;
    }
}
