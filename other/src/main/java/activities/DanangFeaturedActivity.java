package activities;

import annotations.VariantClass;


@VariantClass(
    original = "FeaturedActivity",
    variant  = "Danang"
)
public class DanangFeaturedActivity extends FeaturedActivity {

    public DanangFeaturedActivity() {
    }

    public int render() {
        // Test inheritance
        // super.render();

        // Test function work
        System.out.println("Hello Da Nang !");
        return 1;
    }
}