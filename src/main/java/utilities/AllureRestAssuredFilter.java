package utilities;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.filter.Filter;

/**
 * Factory for the Allure Rest Assured filter that captures HTTP request/response
 * details and attaches them to the active Allure test case.
 */
public final class AllureRestAssuredFilter {

    private AllureRestAssuredFilter() {
    }

    /**
     * Creates a configured Allure filter for Rest Assured.
     * Register via {@code RestAssured.filters(create())} in your base test class.
     */
    public static Filter create() {
        return new AllureRestAssured();
    }
}
