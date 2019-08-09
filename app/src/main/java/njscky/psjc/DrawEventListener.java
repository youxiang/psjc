package njscky.psjc;

import java.util.EventListener;

public interface DrawEventListener extends EventListener {

	void handleDrawEvent(DrawEvent event);
}
