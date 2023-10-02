package com.scheible.risingempire.webapp._selenium;

import static org.openqa.selenium.firefox.FirefoxBinary.Channel.AURORA;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author sj
 */
public class SeleniumHelper {

	public static RemoteWebDriver createFirefoxDriver(final int windowWidth, final int windowHeight) {
		try {
			final String suffix = System.getProperty("os.name").toLowerCase().contains("windows") ? "-win64.exe"
					: "-linux64";
			final File geckodriverFile = new ClassPathResource("geckodriver-v0.33.0" + suffix, SeleniumHelper.class)
					.getFile();
			System.setProperty("webdriver.gecko.driver", geckodriverFile.getCanonicalPath());
		} catch (final IOException ex) {
			throw new UncheckedIOException("An error occurred while getting the canonical path of the geckodriver.",
					ex);
		}

		final RemoteWebDriver driver = new FirefoxDriver(new FirefoxOptions().setBinary(new FirefoxBinary(AURORA)));
		driver.manage().window().setPosition(new Point(0, 0));
		driver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
		return driver;
	}

	public static WebElement findElementInShadowDom(final RemoteWebDriver driver, final WebElement customElement,
			final By by) {
		if (by instanceof By.ById) {
			// Unfortunately no other way access the id... don't try something like this at home... ;-)
			final String id = "#" + by.toString().substring(7);
			return (WebElement) driver.executeScript("return arguments[0].shadowRoot.querySelector('" + id + "')",
					customElement);
		} else if (by instanceof By.ByTagName) {
			// Unfortunately no other way access the id... don't try something like this at home... ;-)
			final String tagName = by.toString().substring(12);
			return (WebElement) driver.executeScript("return arguments[0].shadowRoot.querySelector('" + tagName + "')",
					customElement);
		} else {
			throw new IllegalArgumentException(by + " is not yet supported for Shadow DOM!");
		}
	}

	public static WebElement getDefaultSlotFirstAssignedElement(final RemoteWebDriver driver,
			final WebElement customElement) {
		return (WebElement) driver.executeScript(
				"return arguments[0].shadowRoot.querySelector('slot').assignedElements()[0]", customElement);
	}

	public static void ensureOptionVisible(final RemoteWebDriver driver, final WebElement selectElement,
			final WebElement optionElement) {
		driver.executeScript("arguments[0].scrollTop = arguments[1].offsetTop - arguments[0].offsetHeight",
				selectElement, optionElement);
	}

	public static void hideElement(final RemoteWebDriver driver, final WebElement element) {
		driver.executeScript("arguments[0].style.display = 'none'", element);
	}

	public static void removeElement(final RemoteWebDriver driver, final WebElement element) {
		if (element != null) {
			driver.executeScript("arguments[0].remove();", element);
		}
	}
}
