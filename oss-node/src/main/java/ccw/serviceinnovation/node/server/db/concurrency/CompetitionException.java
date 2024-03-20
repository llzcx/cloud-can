package ccw.serviceinnovation.node.server.db.concurrency;

public class CompetitionException extends RuntimeException{
    public CompetitionException(String msg) {
        super(msg);
    }
}
