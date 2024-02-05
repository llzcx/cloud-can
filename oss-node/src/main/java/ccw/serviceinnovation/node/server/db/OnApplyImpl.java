package ccw.serviceinnovation.node.server.db;

import service.raft.request.*;

public class OnApplyImpl implements OnApply{

    public OnApplyImpl() {

    }

    @Override
    public void get(GetRequest getRequest) {
        System.out.println("get");
    }

    @Override
    public void del(DelRequest delRequest) {
        System.out.println("del");
    }

    @Override
    public void upload(UploadRequest uploadRequest) {
        System.out.println("upload");
    }

    @Override
    public void event(EventRequest eventRequest) {
        System.out.println("event");
    }

    @Override
    public void fragment(FragmentRequest fragmentRequest) {
        System.out.println("fragment");
    }

    @Override
    public void delevent(DelEventRequest delEventRequest) {
        System.out.println("delEvent");
    }

    @Override
    public void merge(MergeRequest mergeRequest) {
        System.out.println("merge");
    }
}
