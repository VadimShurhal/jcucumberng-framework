package jcucumberng.framework.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.Scenario;
import jcucumberng.framework.exceptions.MissingArgumentsException;
import jcucumberng.framework.factory.ByFactory;
import jcucumberng.framework.strings.Messages;

/**
 * {@code Selenium} handles actions for interacting with web applications using
 * the Selenium WebDriver.
 * 
 * @author Kat Rollo <rollo.katherine@gmail.com>
 */
public final class Selenium {

	private static int timeOut = 0;

	// Prevent instantiation
	private Selenium() {
	}

	static {
		try {
			timeOut = Integer.parseInt(ConfigLoader.frameworkConf("webdriver.wait"));
		} catch (NumberFormatException | IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Returns the {@code By} object based on the {@code method} and
	 * {@code selector} delimited by a colon ({@code :}) from
	 * {@code ui-map.properties}.<br>
	 * <br>
	 * Example:
	 * 
	 * <pre>
	 * {@code
	 * ui-map.properties:
	 * income.add=css:button[ng-click='addIncome();']
	 * 
	 * Where:
	 * method = css
	 * selector = button[ng-click='addIncome();']
	 * 
	 * Therefore:
	 * By = By.cssSelector()
	 * }
	 * </pre>
	 * 
	 * @param key the key from {@code ui-map.properties}
	 * @return By - the {@code By} object
	 * @throws IOException
	 */
	public static By by(String key) throws IOException {
		return ByFactory.getInstance(key);
	}

	/**
	 * Returns arbitrary {@code String... keys} as {@code By} array.
	 * 
	 * @param keys the key(s) from {@code ui-map.properties}
	 * @return By[ ] - the By array
	 * @throws IOException
	 */
	public static By[] getBys(String... keys) throws IOException {
		if (0 == keys.length) {
			throw new MissingArgumentsException(Messages.MISSING_ARGS);
		}
		By[] bys = new By[keys.length];
		By by = null;
		for (int ctr = 0; ctr < bys.length; ctr++) {
			by = Selenium.by(keys[ctr]);
			bys[ctr] = by;
		}
		return bys;
	}

	/**
	 * Returns a visible web element. Uses explicit wait.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param keys   the key(s) from {@code ui-map.properties}
	 * @return WebElement - the web element found
	 * @throws IOException
	 */
	public static WebElement getVisibleElement(WebDriver driver, String... keys) throws IOException {
		By[] bys = Selenium.getBys(keys);
		WebElement element = Selenium.wait(driver, timeOut)
				.until(ExpectedConditions.visibilityOfElementLocated(new ByChained(bys)));
		return element;
	}

	/**
	 * Returns visible web elements. Uses explicit wait.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param keys   the key(s) from {@code ui-map.properties}
	 * @return List - the List of web elements found
	 * @throws IOException
	 */
	public static List<WebElement> getVisibleElements(WebDriver driver, String... keys) throws IOException {
		By[] bys = Selenium.getBys(keys);
		List<WebElement> elements = Selenium.wait(driver, timeOut)
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(new ByChained(bys)));
		return elements;
	}

	/**
	 * Returns a List of all Select elements.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param keys   the key(s) from {@code ui-map.properties}
	 * @return List - the List of Select elements
	 * @throws IOException
	 */
	public static List<Select> getSelectElements(WebDriver driver, String... keys) throws IOException {
		List<WebElement> elements = Selenium.getVisibleElements(driver, keys);
		List<Select> selectElements = new ArrayList<>();
		for (WebElement element : elements) {
			selectElements.add(new Select(element));
		}
		return selectElements;
	}

	/**
	 * Returns all text inside the body tag in HTML.
	 * 
	 * @param driver the Selenium WebDriver
	 * @return String - the text within HTML body tags
	 */
	public static String getBodyText(WebDriver driver) {
		return driver.findElement(By.tagName("body")).getText();
	}

	/**
	 * Checks if the element is found on the web page.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param keys   the key(s) from {@code ui-map.properties}
	 * @return {@code true} - if at least one matching element is found on the web
	 *         page
	 * @throws IOException
	 */
	public static boolean isElementPresent(WebDriver driver, String... keys) throws IOException {
		List<WebElement> elements = Selenium.getVisibleElements(driver, keys);
		return 0 < elements.size() ? true : false;
	}

	/**
	 * Clicks an element on the web page.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param keys   the key(s) from {@code ui-map.properties}
	 * @return WebElement - the clickable element
	 * @throws IOException
	 */
	public static WebElement click(WebDriver driver, String... keys) throws IOException {
		WebElement element = Selenium.getVisibleElement(driver, keys);
		element.click();
		return element;
	}

	/**
	 * Enters text into a textfield or textarea.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param text   the text to be entered
	 * @param keys   the key(s) from {@code ui-map.properties}
	 * @return WebElement - the textfield or textarea element
	 * @throws IOException
	 */
	public static WebElement type(WebDriver driver, String text, String... keys) throws IOException {
		WebElement field = Selenium.getVisibleElement(driver, keys);
		field.clear();
		field.sendKeys(text);
		return field;
	}

	/**
	 * Enters text into a textfield or textarea.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param text   the text to be entered
	 * @param field  the textfield or textarea element
	 */
	public static void type(WebDriver driver, String text, WebElement field) {
		field.clear();
		field.sendKeys(text);
	}

	/**
	 * Opens a new window by clicking an element and switches to that window.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param keys   the key(s) from {@code ui-map.properties}
	 * @return String - the handle of the parent window
	 * @throws IOException
	 */
	public static String openWindowByElement(WebDriver driver, String... keys) throws IOException {
		String parentHandle = driver.getWindowHandle(); // Save parent window
		Selenium.click(driver, keys); // Open child window
		boolean isChildWindowOpen = Selenium.wait(driver, timeOut).until(ExpectedConditions.numberOfWindowsToBe(2));
		if (isChildWindowOpen) {
			Set<String> handles = driver.getWindowHandles();
			// Switch to child window
			for (String handle : handles) {
				if (StringUtils.equals(parentHandle, handle)) {
					driver.switchTo().window(handle);
					break;
				}
			}
			driver.manage().window().maximize();
		}
		return parentHandle; // Returns parent window if need to switch back
	}

	/**
	 * Opens a new window by clicking a link and switches to that window.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param url    the link to the child window
	 * @return String - the handle of the parent window
	 */
	public static String openWindowByLink(WebDriver driver, String url) throws IOException {
		String parentHandle = driver.getWindowHandle();
		driver.get(url);
		boolean isChildWindowOpen = Selenium.wait(driver, timeOut).until(ExpectedConditions.numberOfWindowsToBe(2));
		if (isChildWindowOpen) {
			Set<String> handles = driver.getWindowHandles();
			for (String handle : handles) {
				if (StringUtils.equals(parentHandle, handle)) {
					driver.switchTo().window(handle);
					break;
				}
			}
			driver.manage().window().maximize();
		}
		return parentHandle;
	}

	/**
	 * Switches to an existing open window by window title.
	 * 
	 * @param driver      the Selenium WebDriver
	 * @param windowTitle the title of the window
	 * @return String - the handle of the parent window
	 */
	public static String switchToWindowByTitle(WebDriver driver, String windowTitle) {
		Set<String> handles = driver.getWindowHandles();
		String parentHandle = driver.getWindowHandle();
		if (1 < handles.size()) {
			for (String handle : handles) {
				driver.switchTo().window(handle);
				if (StringUtils.equalsIgnoreCase(windowTitle, driver.getTitle())) {
					break;
				}
			}
			driver.manage().window().maximize();
		}
		return parentHandle;
	}

	/**
	 * Scroll the screen left or right.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param xPos   negative value to scroll left, positive value to scroll right
	 */
	public static void scrollHorizontal(WebDriver driver, int xPos) {
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		jsExecutor.executeScript("scroll(" + xPos + ", 0);");
	}

	/**
	 * Scroll the screen up or down.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param yPos   positive value to scroll down, negative value to scroll up
	 */
	public static void scrollVertical(WebDriver driver, int yPos) {
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		jsExecutor.executeScript("scroll(0, " + yPos + ");");
	}

	/**
	 * Scroll to specific element on the web page.
	 * 
	 * @param driver the Selenium WebDriver
	 * @param keys   the key(s) from {@code ui-map.properties}
	 * @throws IOException
	 */
	public static void scrollToElement(WebDriver driver, String... keys) throws IOException {
		By[] bys = Selenium.getBys(keys);
		WebElement element = driver.findElement(new ByChained(bys));
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		jsExecutor.executeScript("arguments[0].scrollIntoView();", element);
	}

	/**
	 * Scroll to specific element on the web page.
	 * 
	 * @param driver  the Selenium WebDriver
	 * @param element the element to scroll to
	 */
	public static void scrollToElement(WebDriver driver, WebElement element) {
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		jsExecutor.executeScript("arguments[0].scrollIntoView();", element);
	}

	/**
	 * Captures and saves screenshot in PNG format. Images are stored in
	 * {@code /target/cucumber-sshots/}.
	 * 
	 * @param driver the Selenium WebDriver
	 * @throws IOException
	 */
	public static void captureScreenshot(WebDriver driver) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append(StringUtils.replace(System.getProperty("user.dir"), "\\", "/"));
		builder.append("/target/cucumber-sshots/sshot_");
		builder.append(System.currentTimeMillis());
		builder.append(".png");
		String screenshot = builder.toString();
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(srcFile, new File(screenshot));
	}

	/**
	 * Captures and embeds screenshot in generated HTML report. Reports can be found
	 * in {@code /target/}.
	 * 
	 * @param scenario the Scenario object
	 * @param driver   the Selenium WebDriver
	 */
	public static void embedScreenshot(WebDriver driver, Scenario scenario) {
		byte[] srcBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		scenario.embed(srcBytes, "image/png");
	}

	/**
	 * Returns the explicit wait object.
	 * 
	 * @param driver  the Selenium WebDriver
	 * @param timeOut the wait time in seconds
	 * @return WebDriverWait - the wait object
	 */
	private static WebDriverWait wait(WebDriver driver, int timeOut) {
		return new WebDriverWait(driver, timeOut);
	}

}
