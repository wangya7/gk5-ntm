package wang.bannong.gk5.ntm.standalone.config;

public enum NtmApiChannel {
    base,
    admin,
    app,
    miniapp,
    h5
    ;
    public static NtmApiChannel of(String key) {
        for (NtmApiChannel item : NtmApiChannel.values()) {
            if (item.name().equals(key)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "NtmApiChannel-" + this.name();
    }

}
