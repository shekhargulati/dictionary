package com.shekhar.dictionary.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.shekhar.dictionary.WordMeaningPair;

@Repository
public class DictionaryDao {

	private static final String ALL_UNIQUE_WORDS = "all-unique-words";
	
	private StringRedisTemplate redisTemplate;

	@Inject
	public DictionaryDao(StringRedisTemplate redisTemplate){
		this.redisTemplate = redisTemplate;
	}
	
	public Long addWordWithItsMeaningToDictionary(String word, String meaning,
			String partOfSpeech) {
		// System.out.println("word : " + word + " , meaning : " + meaning
		// + " , partOfSpeech : " + partOfSpeech);
		Long index = redisTemplate.opsForList().rightPush(word, meaning);
		redisTemplate.opsForSet().add(ALL_UNIQUE_WORDS, word);
		redisTemplate.opsForSet().add(partOfSpeech, word);
		return index;
	}

	public List<String> getAllTheMeaningsForAWord(String word) {
		List<String> meanings = redisTemplate.opsForList().range(word, 0, -1);
		return meanings;
	}

	public void removeWord(String word) {
		redisTemplate.delete(Arrays.asList(word));
	}

	public void removeWords(String... words) {
		redisTemplate.delete(Arrays.asList(words));
	}

	public Set<String> allUniqueWordsInDictionary() {
		Set<String> allUniqueWords = redisTemplate.opsForSet().members(
				ALL_UNIQUE_WORDS);
		return allUniqueWords;
	}

	public int countOfAllUniqueWords() {
		Set<String> allUniqueWords = redisTemplate.opsForSet().members(
				ALL_UNIQUE_WORDS);
		return allUniqueWords.size();
	}

	public WordMeaningPair randomWord() {
		String randomWord = redisTemplate.opsForSet().randomMember(ALL_UNIQUE_WORDS);
		List<String> meanings = redisTemplate.opsForList().range(randomWord, 0, -1);
		WordMeaningPair wordMeaningPair = new WordMeaningPair(randomWord,
				meanings);
		return wordMeaningPair;
	}

	public Set<String> fetchAllWordsThatHavePartOfSpeech(String partOfSpeech) {
		return redisTemplate.opsForSet().members(partOfSpeech);
	}

	public Set<String> findWordWhichAre(String partOfSpeech1,
			String... partOfSpeeches) {
		return redisTemplate.opsForSet().intersect(partOfSpeech1,
				Arrays.asList(partOfSpeeches));
	}

	public Long countOfMembersInASet(String key) {
		return redisTemplate.opsForSet().size(key);
	}

	public Set<String> findWordsThatCanBeEitherOrBoth(String partOfSpeech,
			String... partOfSpeeches) {
		return redisTemplate.opsForSet().union(partOfSpeech,
				Arrays.asList(partOfSpeeches));
	}
}
