package com.scheible.risingempire.webapp.adapter.frontend;

import static com.scheible.risingempire.webapp._selenium.SeleniumHelper.ensureOptionVisible;
import static com.scheible.risingempire.webapp._selenium.SeleniumHelper.findElementInShadowDom;
import static com.scheible.risingempire.webapp._selenium.SeleniumHelper.getDefaultSlotFirstAssignedElement;
import static com.scheible.risingempire.webapp._selenium.SeleniumHelper.hideElement;
import static com.scheible.risingempire.webapp._selenium.SeleniumHelper.removeElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;

import com.galenframework.rainbow4j.ComparisonOptions;
import com.galenframework.rainbow4j.ImageCompareResult;
import com.galenframework.rainbow4j.Rainbow4J;
import com.scheible.risingempire.webapp._selenium.SeleniumHelper;

/**
 * @author sj
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DisabledIfEnvironmentVariable(named = "DISABLE_SELENIUM", matches = "true")
class StorybookSeleniumIT {

	static class StorybookPage {

		private final Actions actions;
		private final WebDriverWait wait;

		@FindBy(tagName = "re-storybook")
		private WebElement storybookEl;

		private StorybookPage(final RemoteWebDriver driver, Duration waitTimeOut) {
			this.actions = new Actions(driver);
			this.wait = new WebDriverWait(driver, waitTimeOut);
		}

		static StorybookPage get(final String host, final int port, final RemoteWebDriver driver,
				Duration waitTimeOut) {
			driver.get("http://" + host + ":" + port + "/storybook.html");

			final StorybookPage storybookPage = new StorybookPage(driver, waitTimeOut);
			PageFactory.initElements(new AjaxElementLocatorFactory(driver, (int) waitTimeOut.toSeconds()),
					storybookPage);
			return storybookPage;
		}

		void hideJsonTextarea() {
			hideElement(driver, findElementInShadowDom(driver, storybookEl, By.id("json")));
		}

		void forEachOption(final Consumer<StoryOption> optionConsumer) {
			final WebElement storiesEl = findElementInShadowDom(driver, storybookEl, By.id("stories"));

			int index = 0;
			for (final WebElement optionEl : storiesEl.findElements(By.tagName("option"))) {
				if (!Boolean.valueOf(optionEl.getAttribute("disabled"))
						&& optionEl.getAttribute("data-animated-story") == null) {
					index++;
					final String value = optionEl.getAttribute("value");

					// make sure that the current option is visible (otheriwse the double click would fail)
					ensureOptionVisible(driver, storiesEl, optionEl);

					optionConsumer.accept(new StoryOption(wait, actions, optionEl, storybookEl, index, value));
				}
			}
		}
	}

	static class StoryOption {

		private final WebDriverWait wait;
		private final Actions actions;
		private final WebElement optionEl;
		private final WebElement storybookEl;
		private final int index;
		private final String storyId;

		StoryOption(WebDriverWait wait, Actions actions, WebElement optionEl, WebElement storybookEl, int index,
				String storyId) {
			this.wait = wait;
			this.actions = actions;
			this.optionEl = optionEl;
			this.storybookEl = storybookEl;
			this.index = index;
			this.storyId = storyId;
		}

		void show(final Consumer<WebElement> loadedCallback) {
			actions.doubleClick(optionEl).build().perform();

			removeElement(driver, wait.until(presenceOfElementLocated(By.id("story-render-done"))));

			WebElement stageContentEl;

			final WebElement pageStoryWrapperEl = findElementInShadowDom(driver, storybookEl,
					By.tagName("re-page-story-wrapper"));

			if (pageStoryWrapperEl == null) {
				stageContentEl = findElementInShadowDom(driver, storybookEl, By.id("stage"));
			} else {
				final WebElement pageEl = getDefaultSlotFirstAssignedElement(driver, pageStoryWrapperEl);
				final WebElement pageModalDialogEl = findElementInShadowDom(driver, pageEl,
						By.tagName("re-modal-dialog"));
				stageContentEl = getDefaultSlotFirstAssignedElement(driver, pageModalDialogEl);
			}

			loadedCallback.accept(stageContentEl);

			// if a re-page-story-wrapper is used close the page with it
			if (pageStoryWrapperEl != null) {
				driver.executeScript("arguments[0].close()", pageStoryWrapperEl);
			}
		}

		int getIndex() {
			return index;
		}

		String getStoryId() {
			return storyId;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(StorybookSeleniumIT.class);

	private static final Function<BufferedImage, Rectangle> WHOLE_SCREENSHOT = image -> new Rectangle(0, 0,
			image.getWidth(), image.getHeight());

	// Workaround for displaced screenshots that include some outside parts and cause false positives.
	private static final Map<String, Function<BufferedImage, Rectangle>> DISPLACED_SCREENSHOT_FIXER = Map.of( //
			"new-game-page", image -> new Rectangle(1, 0, image.getWidth() - 1, image.getHeight()), "tech-page",
			image -> new Rectangle(1, 0, image.getWidth() - 1, image.getHeight()),
			"turn-finished-dialog-waiting-for-others",
			image -> new Rectangle(1, 0, image.getWidth() - 1, image.getHeight()), //
			"space-combat-page", image -> new Rectangle(0, 1, image.getWidth(), image.getHeight() - 1));

	private static RemoteWebDriver driver;

	@LocalServerPort
	private int randomServerPort;

	@BeforeAll
	static void beforeAll() {
		driver = SeleniumHelper.createFirefoxDriver(1280, 854);
		logger.info("webdriver.gecko.driver={}", System.getProperty("webdriver.gecko.driver"));
	}

	@Test
	void testStoryScreenshotCreation() throws IOException {
		final Set<String> storyIdsWithChangedScreenshots = new HashSet<>();

		final StorybookPage storybookPage = StorybookPage.get("localhost", randomServerPort, driver,
				Duration.ofSeconds(5));
		storybookPage.hideJsonTextarea();
		storybookPage.forEachOption(storyOption -> storyOption.show((stageContentEl) -> {
			try {
				final BufferedImage currentScreenshot = ImageIO
						.read(new ByteArrayInputStream(stageContentEl.getScreenshotAs(OutputType.BYTES)));

				final ClassPathResource previousScreenshotResource = new ClassPathResource(
						storyOption.getStoryId() + ".png", getClass());
				if (!previousScreenshotResource.exists()) {
					ImageIO.write(currentScreenshot, "png", new File("./target/" + storyOption.getStoryId() + ".png"));
				}
				assertThat(previousScreenshotResource.exists())
						.as("Story screenshot with name '%s' exists in 'src/test/resources/%s'",
								previousScreenshotResource.getFilename(),
								getClass().getPackageName().replaceAll(Pattern.quote("."), "/"))
						.isTrue();

				final BufferedImage previousScreenshot;
				try (InputStream input = previousScreenshotResource.getInputStream()) {
					previousScreenshot = ImageIO.read(input);
				}

				final boolean sameSize = previousScreenshot.getWidth() == currentScreenshot.getWidth()
						&& previousScreenshot.getHeight() == currentScreenshot.getHeight();

				final ComparisonOptions options = new ComparisonOptions();
				options.setTolerance(5);
				final Rectangle area = DISPLACED_SCREENSHOT_FIXER
						.getOrDefault(storyOption.getStoryId(), WHOLE_SCREENSHOT).apply(currentScreenshot)
						.intersection(WHOLE_SCREENSHOT.apply(previousScreenshot));
				final ImageCompareResult result = Rainbow4J.compare(previousScreenshot, currentScreenshot, area, area,
						options);

				if (!sameSize || result.getPercentage() > 0.001) {
					ImageIO.write(result.getComparisonMap(), "png",
							new File("./target/" + storyOption.getStoryId() + "-diff.png"));
					ImageIO.write(currentScreenshot, "png", new File("./target/" + storyOption.getStoryId() + ".png"));
					storyIdsWithChangedScreenshots.add(storyOption.getStoryId());
				}
			} catch (IOException ex) {
				logger.warn("Problem reading screenshot.", ex);
			}
		}));

		assertThat(storyIdsWithChangedScreenshots).isEmpty();
	}

	@AfterAll
	static void afterAll() {
		driver.quit();
	}
}
