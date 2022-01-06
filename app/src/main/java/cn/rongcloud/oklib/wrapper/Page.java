package cn.rongcloud.oklib.wrapper;

import cn.rongcloud.oklib.wrapper.interfaces.IPage;

public class Page implements IPage {
    private int page;
    private int total;

    public Page(int page, int total) {
        this.page = page;
        this.total = total;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getTotal() {
        return total;
    }
}