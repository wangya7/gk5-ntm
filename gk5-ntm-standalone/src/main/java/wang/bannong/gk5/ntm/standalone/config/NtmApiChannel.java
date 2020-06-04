package wang.bannong.gk5.ntm.standalone.config;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum NtmApiChannel {
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

    public static void main (String[] args) {
        System.out.println(NtmApiChannel.of("admin"));
    }
}
