package ccw.serviceinnovation.split;

public class SplitHandlerFactory {

    private static Integer dataShards;
    private static Integer parityShards;

    public static void initialize(Integer dataShards,Integer parityShards) {
        SplitHandlerFactory.dataShards = dataShards;
        SplitHandlerFactory.parityShards = parityShards;
    }

    public static SplitEncoderHandler createEncoder(SplitEnum splitEnum) {
        if (splitEnum == SplitEnum.RS) {
            return new RsEncoderHandlerImpl(dataShards,parityShards);
        } else {
            throw new RuntimeException("no impl");
        }
    }

    public static SplitDecoderHandler createDecoder(SplitEnum splitEnum) {
        if (splitEnum == SplitEnum.RS) {
            return new RsDecoderHandlerImpl(dataShards,parityShards);
        } else {
            throw new RuntimeException("no impl");
        }
    }
}
