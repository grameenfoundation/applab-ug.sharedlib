package org.purc.purcforms.client.xforms;

import java.util.Vector;

import org.purc.purcforms.client.model.Condition;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.model.SkipRule;

import com.google.gwt.xml.client.Element;

/**
 * Builds relevant attributes of xforms documents from skip rule definition
 * objects.
 * 
 * @author daniel
 * 
 */
public class RelevantBuilder {

	/**
	 * All methods in this class are static and hence we expect no external
	 * Instantiation of this class.
	 */
	private RelevantBuilder() {

	}

	/**
	 * Converts a skip rule definition object to xforms.
	 * 
	 * @param rule
	 *            the skip rule definition object
	 * @param formDef
	 *            the form definition.
	 */
	public static void fromSkipRule2Xform(SkipRule rule, FormDef formDef) {
		String relevant = "";
		Vector conditions = rule.getConditions();
		if(conditions == null)
			return;
		for(int i=0; i<conditions.size(); i++){
			if(relevant.length() > 0)
				relevant += XformBuilderUtil.getConditionsOperatorText(rule.getConditionsOperator());
			relevant += fromSkipCondition2Xform((Condition)conditions.elementAt(i),formDef,rule.getAction());
		}

		Vector actionTargets = rule.getActionTargets();
		for (int i = 0; i < actionTargets.size(); i++) {
			int id = ((Integer) actionTargets.elementAt(i)).intValue();
			QuestionDef questionDef = formDef.getQuestion(id);
			if (questionDef == null)
				continue;

			Element node = questionDef.getBindNode();
			if (node == null)
				node = questionDef.getControlNode();

			if (relevant.trim().length() == 0) {
				node.removeAttribute(XformConstants.ATTRIBUTE_NAME_RELEVANT);
				node.removeAttribute(XformConstants.ATTRIBUTE_NAME_ACTION);
				node.removeAttribute(XformConstants.ATTRIBUTE_NAME_REQUIRED);
			}
			else{
				node.setAttribute(XformConstants.ATTRIBUTE_NAME_RELEVANT, relevant);

				String value = XformConstants.ATTRIBUTE_VALUE_ENABLE;
				if((rule.getAction() & ModelConstants.ACTION_ENABLE) != 0)
					value = XformConstants.ATTRIBUTE_VALUE_ENABLE;
				else if ((rule.getAction() & ModelConstants.ACTION_DISABLE) != 0)
					value = XformConstants.ATTRIBUTE_VALUE_DISABLE;
				else if ((rule.getAction() & ModelConstants.ACTION_SHOW) != 0)
					value = XformConstants.ATTRIBUTE_VALUE_SHOW;
				else if ((rule.getAction() & ModelConstants.ACTION_HIDE) != 0)
					value = XformConstants.ATTRIBUTE_VALUE_HIDE;
				node.setAttribute(XformConstants.ATTRIBUTE_NAME_ACTION, value);

				if ((rule.getAction() & ModelConstants.ACTION_MAKE_MANDATORY) != 0)
					value = XformConstants.XPATH_VALUE_TRUE;
				else if ((rule.getAction() & ModelConstants.ACTION_MAKE_OPTIONAL) != 0)
					value = XformConstants.XPATH_VALUE_FALSE;
				node.setAttribute(XformConstants.ATTRIBUTE_NAME_REQUIRED, value);
			}
		}
	}

	/**
	 * Creates an xforms representation of a skip rule condition.
	 * 
	 * @param condition the condition object.
	 * @param formDef the form definition object to which the skip rule belongs.
	 * @param action the skip rule action to its target questions.
	 * @return the condition xforms representation.
	 */
	private static String fromSkipCondition2Xform(Condition condition, FormDef formDef, int action){
		String relevant = null;

		QuestionDef questionDef = formDef.getQuestion(condition.getQuestionId());
		if (questionDef != null) {
			/*
			 * relevant = questionDef.getBinding();
			 * if(!relevant.contains(formDef.getBinding())) relevant = "/" +
			 * formDef.getBinding() + "/" + questionDef.getBinding();
			 */
			 //the code above between the multiline comments was commented out and 
			 // to fix a bug, the fix which appears below is different from the
			 // one done by the purcforms developer.
			relevant = getFullPath(questionDef, formDef,
					questionDef.getBinding());

			String value = " '" + condition.getValue() + "'";
			if(condition.getValue() != null && condition.getValue().trim().length() > 0){
				if(questionDef.getDataType() == QuestionDef.QTN_TYPE_BOOLEAN || questionDef.getDataType() == QuestionDef.QTN_TYPE_DECIMAL || questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC)
					value = " " + condition.getValue();
			     }
				if(condition.getOperator() == ModelConstants.OPERATOR_BETWEEN)
				    relevant += " " + XformBuilderUtil.getXpathOperator(ModelConstants.OPERATOR_GREATER,action)+value + " and " + relevant + " " + XformBuilderUtil.getXpathOperator( ModelConstants.OPERATOR_LESS,action) + " " + condition.getSecondValue();
				else if(condition.getOperator() == ModelConstants.OPERATOR_NOT_BETWEEN)
					relevant += " " + XformBuilderUtil.getXpathOperator(ModelConstants.OPERATOR_GREATER,action) + " " + condition.getSecondValue() + " or " + relevant + " " + XformBuilderUtil.getXpathOperator( ModelConstants.OPERATOR_LESS,action)+value ;
				else if (condition.getOperator() == ModelConstants.OPERATOR_STARTS_WITH)
					relevant += " starts-with(.,"+ value+")"; 
				else if (condition.getOperator() == ModelConstants.OPERATOR_NOT_START_WITH)
					relevant += " not(starts-with(.,"+ value+"))";
				else if (condition.getOperator() == ModelConstants.OPERATOR_ENDS_WITH)
					relevant += " ends-with(.,"+ value+")"; 
				else if (condition.getOperator() == ModelConstants.OPERATOR_NOT_END_WITH)
					relevant += " not(ends-with(.,"+ value+"))";
				else if (condition.getOperator() == ModelConstants.OPERATOR_CONTAINS)
					relevant += " contains(.,"+ value+")";
				else if (condition.getOperator() == ModelConstants.OPERATOR_NOT_CONTAIN)
					relevant += " not(contains(.,"+ value+"))";
				else
					relevant += " " + XformBuilderUtil.getXpathOperator(condition.getOperator(),action)+value;
				}
			}
				return relevant;
	}


	private static String getFullPath(QuestionDef questionDef, FormDef formDef,
			String questionBinding) {
		String fullPath = null;

		if (questionDef.getParent() instanceof QuestionDef) {

			fullPath = getFullPath((QuestionDef) questionDef.getParent(),
					formDef,
					((QuestionDef) questionDef.getParent()).getBinding());
					
		} else {

			fullPath = "/" + formDef.getBinding(); 
		}
		return fullPath  + "/" + questionBinding;
	}
}
