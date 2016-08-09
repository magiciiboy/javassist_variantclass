package activities;

import annotations.VariantClass;

/*
 * Variant class would not be used or refered by any other class
 * The only class would be used is Parent class (Original class)
 * Based on the variant argurment value
 */

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