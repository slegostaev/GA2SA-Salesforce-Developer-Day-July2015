import akka.actor.ActorRef;
import com.ga2sa.scheduler.Scheduler;
import play.*;
public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {

		Logger.info("Application has started");

		Scheduler.getInstance().tell("start", ActorRef.noSender());

	}

}
