package sumoarena;

import valueobjects.PlayingInfo;
import valueobjects.RoundEndInfo;
import valueobjects.RoundStartInfo;
import valueobjects.AccelerationVector;


public class Player {
	
	/**
	 * The description of the current round
	 */
	private RoundStartInfo roundInfo;
	private AI ai;
	
	/**
	 * Called each time a new round begins
	 * @param RoundStartInfo the data sent by the game server
	 */
	public void onRoundStart(RoundStartInfo roundStartInfo)
	{
		roundInfo = roundStartInfo;
		ai = new AI(roundStartInfo);
	}
	
	/**
	 * Called when server sends a update during the game
	 * @param playingInfo
	 * @return the variation to be applied on the velocity vector, if the sphere is always in the game, or null otherwise
	 */
	public AccelerationVector onPlayRequest(PlayingInfo playingInfo){
		AccelerationVector accelerationVector = null; 
        if (roundInfo != null && playingInfo.getSpheres()[roundInfo.myIndex].inArena)
        {
        	///////////////////////////// insert your code here ///////////////////////
        	
        	accelerationVector = ai.getAccelerationVector(playingInfo);
        	
        	//////////////////////////////////////////////////////////////////////////

        }
        return accelerationVector;
	}
	
	/**
	 * Called each time a round ends
	 * @param roundEndInfo the data sent by the game server
	 */
	public void onRoundEnd(RoundEndInfo roundEndInfo)
	{
		if(roundInfo == null){
			return;
		}
		if(roundEndInfo.roundWinnerIndex == roundInfo.myIndex)
		{
			System.out.println("Congratulations, you won this round!");
		}
		else {
			System.out.println("You lost this round.");
		}
		if(roundEndInfo.gameWinnerIndex == roundInfo.myIndex)
		{
			System.out.println("Congratulations, you won this game!");
		}
		else if (roundEndInfo.gameWinnerIndex != -1){
			System.out.println("You lost this game.");
		}
	}
}
