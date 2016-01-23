package fm.common;

public class BaseEncoding {
    public static final BaseEncoding Base16 = new BaseEncoding(com.google.common.io.BaseEncoding.base16());
    public static final BaseEncoding Base16LowerCase = new BaseEncoding(com.google.common.io.BaseEncoding.base16().lowerCase());

    public static final BaseEncoding Base32 = new BaseEncoding(com.google.common.io.BaseEncoding.base32());
    public static final BaseEncoding Base32LowerCase = new BaseEncoding(com.google.common.io.BaseEncoding.base32().lowerCase());

    public static final BaseEncoding Base64 = new BaseEncoding(com.google.common.io.BaseEncoding.base64());
    //public static final BaseEncoding Base64LowerCase = new BaseEncoding(com.google.common.io.BaseEncoding.base64().lowerCase());

    public static final BaseEncoding Base64Url = new BaseEncoding(com.google.common.io.BaseEncoding.base64Url());

    private com.google.common.io.BaseEncoding baseEncoding;
    public BaseEncoding(com.google.common.io.BaseEncoding enc) {
        baseEncoding = enc;
    }

    public byte[] decode(CharSequence data) { return baseEncoding.decode(data); }
    public byte[] decode(String data) { return baseEncoding.decode(data); }
    public String encode(byte[] bytes) { return baseEncoding.encode(bytes); }
    public String encode(byte[] bytes, int off, int len) { return baseEncoding.encode(bytes, off, len); }
};
