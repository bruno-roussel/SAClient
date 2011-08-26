package valueobjects;

import helpers.Converter;

import java.util.Map;

/**
 * The data transmitted at the end of each round
 * @author peal6230
 */
public class RoundEndInfo 
{
	public  RoundEndInfo(Map<String, Object> roundEndInfoAsJson)
	{
		gameWinnerIndex = Converter.toInt(roundEndInfoAsJson.get("gameWinnerIndex"));
		roundWinnerIndex = Converter.toInt(roundEndInfoAsJson.get("roundWinnerIndex"));
		currentRound = Converter.toInt(roundEndInfoAsJson.get("currentRound"));
	}
	
	/**
	 * The index of the player who won the game, or -1 if the game is not finished yet
	 */
    public int gameWinnerIndex;

	/**
	 * The index of the player who won the round, or -1 if their was no winner
	 */
    public int roundWinnerIndex;   
    
    /**
	 * The number of the current round, starting at 1
	 */
    public int currentRound;

	@Override
	public String toString() {
		return "RoundEndInfo [gameWinnerIndex=" + gameWinnerIndex + ", roundWinnerIndex=" + roundWinnerIndex
				+ ", currentRound=" + currentRound + "]";
	}
    
}
