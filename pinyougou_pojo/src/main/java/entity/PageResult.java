package entity;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: pinyougou_parent
 * @BelongsPackage: com.pinyougou.entity
 * @Author: pky
 * @CreateTime: 2019-05-22 16:48
 * @Description: ${Description}
 * 节省性能
 */
public class PageResult<T> implements Serializable {
    private Long pages;//总页数
    private List<T> rows;//当前页结果

    public PageResult() {
    }

    public PageResult(Long pages, List<T> rows) {
        this.pages = pages;
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "pages=" + pages +
                ", rows=" + rows +
                '}';
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
