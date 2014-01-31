package controllers;

import java.util.*;
import java.io.*;

import models.db1.Location;
import models.db2.User;
import models.db2.CheckIn;
public class Recommender {
	private static long startTime = System.currentTimeMillis();
	private static double[][] slotSimilarity = new double[24][24];
	private static double[][] willingArr;
	
	public Recommender(){
	}
	public ArrayList<String> run(Integer targetUser){
		//List<models.db2.User> allUsers = models.db2.User.find.all();
		//List<models.db2.Location> allLocs = models.db2.Location.find.all();
		//List<models.db2.User> usersFromDB = models.db2.User.find.all();
		List<models.db2.CheckIn> checkIns = models.db2.CheckIn.find.all();
		HashMap<Integer, HashMap<Location, HashSet<Short>>> allUsers = new HashMap<Integer,HashMap<Location,HashSet<Short>>>();
		//HashMap<User, HashMap<Location, HashSet<Short>>> allUsers = new HashMap<User, HashMap<Location, HashSet<Short>>>();
		ArrayList<String> returnString= new ArrayList<String>();
		ArrayList<Location> allLocs = new ArrayList<Location>();
		for (int i=0; i < checkIns.size(); i++){
			Integer uId = checkIns.get(i).userId;
			Location l = new Location();
			l.locId = checkIns.get(i).locId;
			l.latitude = checkIns.get(i).latitude;
			l.longitude = checkIns.get(i).longitude;
			
			Short timeOfCheckIn = Integer.valueOf(checkIns.get(i).slot).shortValue();
			HashSet<Short> times = new HashSet<Short>();
			times.add(timeOfCheckIn);		
			HashMap<Location, HashSet<Short>> locTime = new HashMap<Location, HashSet<Short>>();
			locTime.put(l, times);
			if (!allUsers.containsKey(uId)){
				allUsers.put(uId, locTime);
			}
			else{
				if (allUsers.get(uId).containsKey(l)){
					if (!allUsers.get(uId).get(l).contains(timeOfCheckIn)){
						allUsers.get(uId).get(l).add(timeOfCheckIn);
					}
				}
				else{
					allUsers.get(uId).put(l, times);
				}
			}
			if(!allLocs.contains(l)){
				allLocs.add(l);
			}
		}
		for (Short s=0; s < 24; s++){
			for (Short x=0; x < 24; x++){
					slotSimilarity[s][x] = similarityBetweenSlots(s, x, allUsers); 
			}
		}
		//4.1: eq[6]
		willingArr = new double[allLocs.size()][allLocs.size()];  //willingness array of any user to visit far away POI
		
		for (int i=0; i < willingArr.length; i++){
			for (int j=0; j < willingArr[0].length; j++){
				if (i != j){
					double a = 0.08984445569632919;       //It may be wrong value for a, taken from code of Phd student. 
					double k = -1.1109234088432385;
					double dist = calculateDistance(allLocs.get(i), allLocs.get(j));
					willingArr[i][j] = a * Math.pow(dist, k);
				}
				else willingArr[i][j] = 0;
			}
		}
		//Part 4 of paper. Spatial influence.
		//User targetUser = new User("USER_1815");
		//User targetUser = new User("USER_689");
		Short atTime = 20;
		double[] recScoreForLocsSpatial = new double[allLocs.size()];
		for (int z=0; z < recScoreForLocsSpatial.length; z++){
			if (allUsers.get(targetUser).containsKey(allLocs.get(z))){   //Do not consider visited locations
				continue;
			}
			double numerator1=0, denominator1=0;
			double numerator2=0, denominator2=0;
			double priorProbability = 0;
			for (Map.Entry<Integer, HashMap<Location, HashSet<Short>>> entry : allUsers.entrySet()){
				HashMap<Location, HashSet<Short>> locs = entry.getValue();
				if (locs.containsKey(allLocs.get(z)) && locs.get(allLocs.get(z)).contains(atTime)){
					numerator1++;
				}
				if (locs.containsKey(allLocs.get(z))){
					numerator2++;
				}
				for (Map.Entry<Location, HashSet<Short>> l : locs.entrySet() ){
					if (l.getValue().contains(atTime)){
						denominator1++;
					}
					denominator2 += l.getValue().size();
				}
			}
			double betta = 0.9;
			priorProbability = betta * numerator1 / denominator1 +  (1 - betta) * numerator2 / denominator2;
			HashMap<Location, HashSet<Short>> locsOfUser = allUsers.get(targetUser);
			double product = 1;
			for (Map.Entry<Location, HashSet<Short>> entry : locsOfUser.entrySet()){
				int indexI = allLocs.indexOf(entry.getKey());
				product *= calculateProbability(indexI, z);
			}
			recScoreForLocsSpatial[z] = priorProbability * product; 
		}
		
		//End of part 4.
		//Part 3 of paper. Temporal Influence
		double[] recScoreForLocsTemporal = new double[allLocs.size()];
		/*
		for(int z=0; z < recScoreForLocsTemporal.length; z++){	
			if (allUsers.get(targetUser).containsKey(allLocs.get(z))){       //Do not consider visited locations
				continue;
			}
			double numerator = 0;
			double denominator = 0;
			for (Map.Entry<User, HashMap<Location, HashSet<Short>>> entry : allUsers.entrySet()){
				double similarity = cosineSimilarityBetweenUsers(allUsers.get(targetUser), entry.getValue());
				double magnitude = 0;
				for (Short i=0; i < slotSimilarity[0].length; i++){
					magnitude += computeNewValue(entry.getValue(), allLocs.get(z), i) * slotSimilarity[atTime][i];
				}
				numerator += similarity * magnitude;
				denominator += similarity; 
			}		
			if (denominator != 0 && numerator != 0){
				recScoreForLocsTemporal[z] = numerator/denominator;
				long endTime1 = System.currentTimeMillis();
				System.out.println("Loc = " + allLocs.get(z).locId + " score = "+ recScoreForLocsTemporal[z] + " time = " + (endTime1 - startTime));
			}
		}*/
		//End of part 3.
		
		double maxSpatial = 0;
		double maxTemporal = 1;
		for (int i=0; i < recScoreForLocsSpatial.length; i++){
			if (recScoreForLocsSpatial[i] > maxSpatial){
				maxSpatial = recScoreForLocsSpatial[i];
			}
			if (recScoreForLocsTemporal[i] > maxTemporal){
				maxTemporal = recScoreForLocsTemporal[i];
			}
		}
		double[] combinedScoreForLocs = new double[allLocs.size()];
		for (int i=0; i < combinedScoreForLocs.length; i++){
			double alfaTuning = 1;
			combinedScoreForLocs[i] = alfaTuning * (recScoreForLocsSpatial[i] / maxSpatial) + (1 - alfaTuning) * (recScoreForLocsTemporal[i] / maxTemporal);
		}
		
		HashMap<Location, Double> unsortResult = new HashMap<Location, Double>();
		for (int i=0; i < allLocs.size(); i++){
			unsortResult.put(allLocs.get(i), combinedScoreForLocs[i]);
		}
		LinkedHashMap<Location, Double> sortedMap = sortByComparator(unsortResult);
		ArrayList<Location> sortedLocations = new ArrayList<Location>();
		ArrayList<Double> sortedScores = new ArrayList<Double>();
		sortedLocations.addAll(sortedMap.keySet());
		sortedScores.addAll(sortedMap.values());
		for (int i=sortedScores.size()-1; i >= sortedScores.size() - 10; i-- ){
			double normalizedScore = (sortedScores.get(i) - sortedScores.get(0)) / (sortedScores.get(sortedScores.size()-1) - sortedScores.get(0));
			//System.out.println("Location = " + sortedLocations.get(i).locId + " score = " + normalizedScore);
			returnString.add("Location = " + sortedLocations.get(i).locId + " score = " + normalizedScore+"\n");
		}
		long endTime = System.currentTimeMillis();
        System.out.println("It took " + (endTime - startTime) + " milliseconds");
        return returnString;
	}
	public static double calculateProbability(int j, int i){
		double numerator = willingArr[i][j];
		double denominator = 0;
		for (int k=0; k < willingArr.length; k++){
			denominator += willingArr[i][k]; 
		}
		if (denominator != 0){
			return numerator / denominator;
		}
		return 0;
	}
	public static double calculateDistance(Location loc1, Location loc2){
		double lat1 = loc1.latitude;
		double lon1 = loc1.longitude;
		double lat2 = loc2.latitude;
		double lon2 = loc2.longitude;
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;		  
		return (dist);
	}
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
	public static double similarityBetweenSlots(Short s, Short x, HashMap<Integer, HashMap<Location, HashSet<Short>>> allUsers){
		double averageSimilarity = 0;
		ArrayList<Double> cosArr = new ArrayList<Double>();
		
		double dotProduct;
		double magnitude1;
		double magnitude2;
		
		for (Map.Entry<Integer, HashMap<Location, HashSet<Short>>> entry : allUsers.entrySet()) {
		    HashMap<Location, HashSet<Short>> locsOfU = entry.getValue();
		    dotProduct = 0;
			magnitude1 = 0;
			magnitude2 = 0;
			
			for (Map.Entry<Location, HashSet<Short>> entryLoc : locsOfU.entrySet()){
				if (entryLoc.getValue().contains(s) && entryLoc.getValue().contains(x)){
					dotProduct += 1;
				}
				if (entryLoc.getValue().contains(s)){
					magnitude1 += 1;
				}
				if (entryLoc.getValue().contains(x)){
					magnitude2 += 1;
				}
			}	
			magnitude1 = Math.sqrt(magnitude1);
			magnitude2 = Math.sqrt(magnitude2);
			if (magnitude1 != 0 && magnitude2 != 0){
				cosArr.add(dotProduct / (magnitude1 * magnitude2));
			}
		}	
		for (Double d : cosArr){
			averageSimilarity = averageSimilarity + d.doubleValue();
		}
		averageSimilarity = averageSimilarity / allUsers.size();
		return averageSimilarity;
	}
	public static double cosineSimilarityBetweenUsers(HashMap<Location, HashSet<Short>> userU, HashMap<Location, HashSet<Short>> userV){
		double cU, cV;
		double dotProduct = 0;
		double magnitude1 = 0;
		double magnitude2 = 0;
		
		HashSet<Location> locsOfTwoUsers = new HashSet<Location>();
		locsOfTwoUsers.addAll(userU.keySet());
		locsOfTwoUsers.addAll(userV.keySet());
		
		for (Short j=0; j < 24; j++){
			cU = 0;
			cV = 0;
			Iterator<Location> itr = locsOfTwoUsers.iterator();
			while (itr.hasNext()){
				Location l = itr.next();
				cU = computeNewValue(userU, l, j);
				cV = computeNewValue(userV, l, j);
				dotProduct += cU * cV;
				magnitude1 += Math.pow(cU, 2);
				magnitude2 += Math.pow(cV, 2);
			}
		}
		magnitude1 = Math.sqrt(magnitude1);
		magnitude2 = Math.sqrt(magnitude2);
		
		if (magnitude1 != 0 && magnitude2 !=0){
			return (dotProduct / (magnitude1 * magnitude2));
		}
		return 0;
	}

	public static double computeNewValue(HashMap<Location, HashSet<Short>> user, Location loc, Short atTime){
		double numerator = 0;
		double denominator = 0;
		
		for (Short i=0; i < slotSimilarity[atTime].length; i++){
			if (user.containsKey(loc) && user.get(loc).contains(i)){
				numerator += slotSimilarity[atTime][i] * 1;
			}
			denominator += slotSimilarity[atTime][i];
		}
		if (denominator != 0){
			return numerator / denominator;
		}
		return 0;
	}
	private static LinkedHashMap<Location, Double> sortByComparator(HashMap<Location, Double> unsortedMap){
		List<Map.Entry<Location, Double>> list = new LinkedList<Map.Entry<Location, Double>>(unsortedMap.entrySet());
		/*Collections.sort(list, new Comparator(){
			public int compare(Object o1, Object o2){
				return ((Comparable) ((Map.Entry<Location, Double>) (o1)).getValue())
						.compareTo(((Map.Entry<Location, Double>) (o2)).getValue());
			}
		});*/
		Collections.sort(list, new Comparator<Map.Entry<Location, Double>>(){
			public int compare(Map.Entry<Location, Double> o1, Map.Entry<Location, Double> o2){
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		//put sorted list into map again 
		//LinkedHashMap make sure order in which keys were inserted
		LinkedHashMap<Location, Double> sortedMap = new LinkedHashMap<Location, Double>();
		for (Iterator<Map.Entry<Location, Double>> it = list.iterator(); it.hasNext();){
			Map.Entry<Location, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}

