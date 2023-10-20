package ssbd01.moa.managers;

import jakarta.ejb.Local;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Qualifier;
import jakarta.resource.spi.ConfigProperty;
import ssbd01.common.CommonManagerLocalInterface;
import ssbd01.entities.Category;

import java.util.List;

@Local
public interface CategoryManagerLocal extends CommonManagerLocalInterface {


    List<Category> getAllCategories();

    Category createCategory(Category category);

    Category getCategory(Long id);

    Category editCategory(Long id, Category category, Long version);

    Category findByName(String name);
}
