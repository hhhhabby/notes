接口设计一致性
两种写法都是合理的，主要取决于 API 设计风格和前端需求
如果前端不需要具体的返回信息，使用 Result 即可
如果需要返回提示信息，则使用 Result<String>




// 方式一：都使用 Result
@DeleteMapping
@ApiOperation ("菜品批量删除")
public ==Result== delete (@RequestParam List<Long> ids){
    // ...
    Return Result.Success ();
}

// 方式二：都使用 Result<String>  
@DeleteMapping
@ApiOperation ("菜品批量删除")
public ==Result<String>== delete (@RequestParam List<Long> ids){
    // ...
    Return Result.Success ("删除成功");
}