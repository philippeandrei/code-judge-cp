import com.google.inject.AbstractModule;
import service.IProblemService;
import service.IUserService;
import service.ProblemService;
import service.UserService;

public class AppModule extends AbstractModule {
  @Override
  protected void configure() {

    bind(IProblemService.class).to(ProblemService.class);
    bind(IUserService.class).to(UserService.class);
  }
}
