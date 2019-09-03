package njscky.psjc.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;

public class OptionalProperty extends Property {
    public String[] options;

    public OptionalProperty(@NonNull String[] options) {
        super();
        this.options = options;
    }

    public OptionalProperty(String name, String value, @NonNull String[] options) {
        super(name, value);
        this.options = options;
    }

    public int getSelection() {
        for (int i = 0; i < options.length; i++) {
            if (TextUtils.equals(options[i], value)) {
                return i;
            }
        }
        return -1;
    }
}
