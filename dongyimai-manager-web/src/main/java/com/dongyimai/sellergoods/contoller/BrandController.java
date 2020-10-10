package com.dongyimai.sellergoods.contoller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dongyimai.entity.PageResult;
import com.dongyimai.entity.Result;
import com.dongyimai.pojo.TbBrand;
import com.dongyimai.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand/")
public class BrandController {

    @Reference
    private BrandService service;

    /**
     * 模糊搜索加分页
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    @RequestMapping("search")
    public PageResult search(int pageNum,int pageSize,@RequestBody TbBrand brand){
        return service.search(pageNum,pageSize,brand);
    }

    /**
     * 查询所有
     * @return
     */
    @RequestMapping("findAll")
    public List<Map> findAll(){
        return service.findAll();
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("findPage")
    public PageResult findPage(int pageNum,int pageSize){
        return service.findPage(pageNum,pageSize);
    }

    /**
     * 添加
     * @param brand
     * @return
     */
    @RequestMapping("add")
    public Result add(@RequestBody TbBrand brand){
        try {
            service.add(brand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    /**
     * 通过主键查询
     * @param id
     * @return
     */
    @RequestMapping("getById")
    public TbBrand getById(Long id){
        return service.getById(id);
    }

    /**
     * 修改
     * @param brand
     * @return
     */
    @RequestMapping("update")
    public Result update(@RequestBody TbBrand brand){
        try {
            service.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"修改失败");
        }
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    public Result delete(long[] ids){
        try {
            service.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"删除失败");
        }
    }
}
