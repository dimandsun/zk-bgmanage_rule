package cn.lxt6.model.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author chenzy
 *
 * @since 2020-02-27
 */
public class Page<Data> {
    private static final Integer PAGESIZE = 10;


    //当前页 从1开始
    private Integer pageIndex=null;
    //当前页 从1开始 .当前页有两个是因为前期没有规定返回pageIndex还是currentPage,两个都有用，
    private Integer currentPage=null;

    //每页显示条数
    private Integer pageSize = null;
    //总页数
    @JsonProperty("totalPage")
    private Integer pageCount;

    //总记录数
    @JsonProperty("totalRecord")
    private Integer recordCount;

    //分页数据
    @JsonProperty("Records")
    private List<Data> dataList;
    //起始行
    @JsonProperty("offset")
    private Integer beginRecord;

    public Page() {
    }

    public Page(Integer currentPage) {
        this.pageIndex=this.currentPage = currentPage;
    }
    /**
     *获取起始行
     */
    public Integer getBeginRecord() {
        if (beginRecord==null){
            setBeginRecord();
        }
        return beginRecord;
    }
    public void setBeginRecord() {
        if (currentPage==null||currentPage<1){
            pageIndex=currentPage=1;
        }
        if (pageIndex==null||pageIndex<1){
            pageIndex=currentPage=1;
        }
        if (pageSize==null){
            pageSize=PAGESIZE;
        }
        this.beginRecord = (currentPage-1)*pageSize;
    }
    public Page(Integer currentPage, Integer recordCount) {
        setCurrentPage(currentPage);
        setRecordCount(recordCount);
    }
    /**
     * 设置当前页的同时设置当前起始行
     * @param currentPage
     */
    public void setCurrentPage(Integer currentPage) {
        this.pageIndex=this.currentPage = currentPage;
        setBeginRecord();
    }

    /**
     * 设置总记录数时设置页数
     * 页数=总记录数/每页记录数 除不尽的加1
     * @param recordCount
     */
    public void setRecordCount(Integer recordCount) {
        this.recordCount = recordCount;
        this.pageCount = recordCount/pageSize;
        if (recordCount%pageSize!=0){
            this.pageCount++;
        }
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;

    }

    public static Integer getPAGESIZE() {
        return PAGESIZE;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }


    public void setBeginRecord(Integer beginRecord) {
        this.beginRecord = beginRecord;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageCount() {
        return pageCount;
    }



    public Integer getRecordCount() {
        return recordCount;
    }



    public List<Data> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }
}
