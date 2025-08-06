package model;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = "-t", description = "The type of request (set, get, delete)")
    private String type = null;

    @Parameter(names = "-k", description = "Key")
    private String key = null;

    @Parameter(names = "-v", description = "Value")
    private String value = null;

    @Parameter(names = "-in, --file",
               description = "File to read JSON requests from. If this is specified, any other option(s) will be ignored.")
    private String filename = null;

    public String request() {
        return type;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }

    public String filename() {
        return filename;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"type\":\"").append(type).append("\"");
        if (key != null) {
            sb.append(",\"key\":\"").append(key).append("\"");
        }
        if (value != null) {
            sb.append(",\"value\":\"").append(value).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }
}
