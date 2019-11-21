package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import content.Symbol;
import content.Tag;

/**
 * Model to serialize associations between symbols and their tags
 * @author Etienne Hüsler
 * @version 2.0
 */
public class AssociationsModel implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Map<String, ArrayList<Tag>> associations;
	
	public AssociationsModel(ArrayList<Symbol> symbols)
	{
		associations = new HashMap<>();
		for (int i = 0; i < symbols.size(); i++)
		{
			Symbol symbol = symbols.get(i);
			if (symbol.getName() != null && symbol.getTags() != null)
			{
				associations.put(symbol.getName(), symbol.getTags());
			}
		}
	}
	
	public void assignAssociations(ArrayList<Symbol> symbols)
	{
		ArrayList<Tag> newTags = new ArrayList<>();
		for (Map.Entry<String, ArrayList<Tag>> association : associations.entrySet()) 
		{
		    for (int i = 0; i < symbols.size(); i++)
		    {
		    	Symbol symbol = symbols.get(i);
		    	if (symbol.getName().equals(association.getKey()))
		    	{
		    		symbol.setTags(association.getValue());
		    		newTags.addAll(association.getValue());
		    	}
		    }
		}
	}
}
