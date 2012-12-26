package com.shekhar.dictionary.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.shekhar.dictionary.WordMeaningPair;
import com.shekhar.dictionary.config.DictionaryConfig;
import com.shekhar.dictionary.config.LocalRedisConfig;

@ContextConfiguration(classes = { DictionaryConfig.class,
		LocalRedisConfig.class })
@ActiveProfiles("local")
@RunWith(SpringJUnit4ClassRunner.class)
public class DictionaryDaoTest {

	@Inject
	private DictionaryDao dictionaryDao;

	@Inject
	private StringRedisTemplate redisTemplate;

	@After
	public void tearDown() {
		redisTemplate.getConnectionFactory().getConnection().flushDb();
	}

	@Test
	public void testAddWordWithItsMeaningToDictionary() {
		String meaning = "To move forward with a bounding, drooping motion.";
		Long index = dictionaryDao.addWordWithItsMeaningToDictionary("lollop",
				meaning, "verb");
		assertThat(index, is(notNullValue()));
		assertThat(index, is(equalTo(1L)));
		List<String> allMeanings = dictionaryDao
				.getAllTheMeaningsForAWord("lollop");
		assertEquals(meaning, allMeanings.get(0));
	}

	@Test
	public void shouldAddMeaningToAWordIfItExists() {
		Long index = dictionaryDao.addWordWithItsMeaningToDictionary("lollop",
				"To move forward with a bounding, drooping motion.", "verb");
		assertThat(index, is(notNullValue()));
		assertThat(index, is(equalTo(1L)));
		index = dictionaryDao.addWordWithItsMeaningToDictionary("lollop",
				"To hang loosely; droop; dangle.", "verb");
		assertThat(index, is(equalTo(2L)));
	}

	@Test
	public void shouldGetAllTheMeaningForAWord() {
		setupOneWord();
		List<String> allMeanings = dictionaryDao
				.getAllTheMeaningsForAWord("lollop");
		assertThat(allMeanings.size(), is(equalTo(2)));
		assertThat(
				allMeanings,
				hasItems("To move forward with a bounding, drooping motion.",
						"To hang loosely; droop; dangle."));
	}

	@Test
	public void shouldDeleteAWordFromDictionary() throws Exception {
		setupOneWord();
		dictionaryDao.removeWord("lollop");
		List<String> allMeanings = dictionaryDao
				.getAllTheMeaningsForAWord("lollop");
		assertThat(allMeanings.size(), is(equalTo(0)));
	}

	@Test
	public void shouldDeleteMultipleWordsFromDictionary() {
		setupTwoWords();
		dictionaryDao.removeWords("fain", "lollop");
		List<String> allMeaningsForLollop = dictionaryDao
				.getAllTheMeaningsForAWord("lollop");
		List<String> allMeaningsForFain = dictionaryDao
				.getAllTheMeaningsForAWord("fain");
		assertThat(allMeaningsForLollop.size(), is(equalTo(0)));
		assertThat(allMeaningsForFain.size(), is(equalTo(0)));
	}

	@Test
	public void shouldGetAllUniqueWordsInTheDictionary() throws Exception {
		setupTwoWords();
		Set<String> allUniqueWords = dictionaryDao.allUniqueWordsInDictionary();
		assertThat(allUniqueWords.size(), is(equalTo(2)));
		assertThat(allUniqueWords, hasItems("lollop", "fain"));
	}

	@Test
	public void shouldGetCountOfAllUniqueWordsInTheDictionary()
			throws Exception {
		setupTwoWords();
		int countOfAllUniqueWords = dictionaryDao.countOfAllUniqueWords();
		assertThat(countOfAllUniqueWords, is(equalTo(2)));
	}

	@Ignore
	public void shouldGiveMeARandomWord() throws Exception {
		setupTwoWords();
		WordMeaningPair wordMeaningPair = dictionaryDao.randomWord();
		assertThat(wordMeaningPair, is(notNullValue()));
		assertThat(wordMeaningPair.getWord(), is(notNullValue()));
		assertThat(!wordMeaningPair.getMeanings().isEmpty(), is(true));
	}

	private void setupTwoWords() {
		setupOneWord();
		dictionaryDao.addWordWithItsMeaningToDictionary("fain",
				"Content; willing.", "adjective");
		dictionaryDao.addWordWithItsMeaningToDictionary("fain",
				"Archaic: Constrained; obliged.", "adjective");
	}

	private void setupOneWord() {
		dictionaryDao.addWordWithItsMeaningToDictionary("lollop",
				"To move forward with a bounding, drooping motion.", "verb");
		dictionaryDao.addWordWithItsMeaningToDictionary("lollop",
				"To hang loosely; droop; dangle.", "verb");
	}

}