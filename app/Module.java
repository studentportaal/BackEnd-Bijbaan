import com.google.inject.AbstractModule;

/**
 * @author Max Meijer
 * Created on 02/04/2019
 */
public class Module extends AbstractModule {
    protected void configure() {
        bind(Bootstrap.class).asEagerSingleton();
    }
}
