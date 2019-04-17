import com.google.inject.AbstractModule;

/**
 * @author Max Meijer
 * Created on 02/04/2019
 */

// Play automatically starts any class named '[Something]Module'
public class Module extends AbstractModule {
    protected void configure() {
        bind(Bootstrap.class).asEagerSingleton();
    }
}
