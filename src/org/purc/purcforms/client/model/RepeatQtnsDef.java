package org.purc.purcforms.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

/**
 * Definition for repeat sets of questions. Basically this is just a specialized
 * collection of a set of repeating questions, together with reference to their
 * parent question.
 * 
 * @author daniel
 * 
 */
public class RepeatQtnsDef implements Serializable {

	/** A list of questions (QuestionDef objects) on a repeat questions row. */
	private Vector questions;

	/** Reference to the parent question. */
	private QuestionDef qtnDef;

	/**
	 * The maximum number of rows that this repeat questions definition can
	 * have.
	 */
	private byte maxRows = -1;

	/**
	 * Creates a new repeat questions definition object.
	 */
	public RepeatQtnsDef() {

	}

	/** Copy Constructor. */
	public RepeatQtnsDef(RepeatQtnsDef repeatQtnsDef) {
		// setQtnDef(new QuestionDef(repeatQtnsDef.getQtnDef()));
		setQtnDef(repeatQtnsDef.getQtnDef());
		copyQuestions(repeatQtnsDef.getQuestions());
	}

	public RepeatQtnsDef(QuestionDef qtnDef) {
		setQtnDef(qtnDef);
	}

	public RepeatQtnsDef(QuestionDef qtnDef, Vector questions) {
		this(qtnDef);
		setQuestions(questions);
	}

	public QuestionDef getQtnDef() {
		return qtnDef;
	}

	public void setQtnDef(QuestionDef qtnDef) {
		this.qtnDef = qtnDef;
	}

	public Vector getQuestions() {
		return questions;
	}

	public int size() {
		if (questions == null)
			return 0;
		return questions.size();
	}

	public void addQuestion(QuestionDef qtn) {
		addQuestion(qtn, null);
	}

	public void addQuestion(QuestionDef qtn, QuestionDef refQtn) {
		if (questions == null)
			questions = new Vector();

		//qtn.setId((byte)(questions.size()+1)); id should be set somewhere else
		if(refQtn == null)
			questions.addElement(qtn);
		else
			questions.add(questions.indexOf(refQtn) + 1, qtn);
	}

	public void removeQuestion(QuestionDef qtnDef, FormDef formDef){
		if(qtnDef.getControlNode() != null && qtnDef.getControlNode().getParentNode() != null)
			qtnDef.getControlNode().getParentNode().removeChild(qtnDef.getControlNode());
		if(qtnDef.getDataNode() != null && qtnDef.getDataNode().getParentNode() != null)
			qtnDef.getDataNode().getParentNode().removeChild(qtnDef.getDataNode());
		if(qtnDef.getBindNode() != null && qtnDef.getBindNode().getParentNode() != null)
			qtnDef.getBindNode().getParentNode().removeChild(qtnDef.getBindNode());
		
		if(formDef != null)
			formDef.removeQtnFromRules(qtnDef);

		questions.removeElement(qtnDef);
	}

	public void setQuestions(Vector questions) {
		this.questions = questions;
	}

	public String getText() {
		if (qtnDef != null)
			return qtnDef.getText();
		return null;
	}

	public QuestionDef getQuestion(int id) {
		if (questions == null)
			return null;

		for (int i = 0; i < getQuestions().size(); i++) {
			QuestionDef def = (QuestionDef) getQuestions().elementAt(i);
			if (def.getId() == id)
				return def;
		}

		return null;
	}

	public QuestionDef getQuestionAt(int index) {
		return (QuestionDef) questions.elementAt(index);
	}

	public int getQuestionsCount() {
		if (questions == null)
			return 0;
		return questions.size();
	}

	private void copyQuestions(Vector questions) {
		if (questions == null)
			return;

		this.questions = new Vector();
		for(int i=0; i<questions.size(); i++)
			this.questions.addElement(new QuestionDef((QuestionDef)questions.elementAt(i),qtnDef));
	}

	public void moveQuestionUp(QuestionDef questionDef) {
		PageDef.moveQuestionUp(questions, questionDef);
	}

	public void moveQuestionDown(QuestionDef questionDef) {
		PageDef.moveQuestionDown(questions, questionDef);
	}

	public void updateDoc(Document doc, Element xformsNode, FormDef formDef, Element formNode, Element modelNode,Element groupNode, boolean withData, String orgFormVarName){
		if(questions == null)
			return;
		
		for(int i=0; i<questions.size(); i++){
			QuestionDef questionDef = (QuestionDef)questions.elementAt(i);
			questionDef.updateDoc(doc,xformsNode,formDef,qtnDef.getDataNode(),modelNode,qtnDef.getControlNode(), true /*false*/ ,withData,orgFormVarName, qtnDef.getBinding());
		}
	}
	
	/**
	 * Gets a question identified by a variable name.
	 * 
	 * @param varName
	 *            the string identifier of the question.
	 * @return the question reference.
	 */
	public QuestionDef getQuestion(String varName) {
		if (varName == null || questions == null)
			return null;
		ArrayList<QuestionDef> allQuestions = getAllQuestions();
		if (allQuestions != null && allQuestions.size() > 0) {
			for (QuestionDef question : allQuestions) {
				if (question.getBinding().equals(varName)) {
					return question;
				}
			}
		}
		return null;	
	}

	/**
	 * Gets all questions of this Repeat.
	 * question including any nested 
	 * child questions
	 */
	public ArrayList<QuestionDef> getAllQuestions() {
		ArrayList<QuestionDef> allQuestions = new ArrayList<QuestionDef>();
		for (int i = 0; i < questions.size(); i++) {
			QuestionDef question = (QuestionDef)questions.elementAt(i);
			addChildQuestion(allQuestions, question);
		}
		return allQuestions;
	}
	
	/**
	 * Given a question, add it to the list of questions.
	 * and if it is a repeat type, add its child questions too
	 * @param list
	 *     the list of current questions.
	 * @param question
	 *     the current question to be queried for child questions if any.
	 * @return the updated list of questions.
	 */
	public ArrayList<QuestionDef> addChildQuestion(ArrayList<QuestionDef> list, QuestionDef question) {
		list.add(question);
		if (question.getDataType() == QuestionDef.QTN_TYPE_REPEAT) {
			Vector qtns = question.getRepeatQtnsDef().getQuestions();
			if (qtns != null && qtns.size() > 0) {
				for (int i = 0; i < qtns.size(); i++) {
					QuestionDef qtn = (QuestionDef)qtns.get(i);
					addChildQuestion(list, qtn);
				}
			}
		}
		return list;
	}
	
	public QuestionDef getQuestionWithText(String text) {
		if (text == null || questions == null)
			return null;

		for (int i = 0; i < questions.size(); i++) {
			QuestionDef questionDef = (QuestionDef) questions.elementAt(i);
			if (questionDef.getText().equals(text))
				return questionDef;
		}
		return null;
	}

	public void refresh(RepeatQtnsDef pepeatQtnsDef) {
		Vector questions2 = pepeatQtnsDef.getQuestions();
		if (questions == null || questions2 == null)
			return;

		Vector<QuestionDef> orderedQtns = new Vector<QuestionDef>();

		for (int index = 0; index < questions2.size(); index++) {
			QuestionDef qtn = (QuestionDef) questions2.get(index);
			QuestionDef questionDef = getQuestion(qtn.getBinding());
			if (questionDef != null) {
				questionDef.refresh(qtn);
				orderedQtns.add(questionDef); // add the question in the order
												// it was before the refresh.
			}
		}

		// now add the new questions which have just been added by refresh.
		int count = questions.size();
		for (int index = 0; index < count; index++) {
			QuestionDef questionDef = getQuestionAt(index);
			if (pepeatQtnsDef.getQuestion(questionDef.getBinding()) == null)
				orderedQtns.add(questionDef);
		}

		questions = orderedQtns;
	}

	/**
	 * Updates the xforms instance data nodes referenced by this repeat
	 * questions definition and its children.
	 * 
	 * @param parentDataNode
	 *            the parent data node for this repeat questions definition.
	 */
	public void updateDataNodes(Element parentDataNode) {
		if (questions == null)
			return;

		for(int i=0; i<questions.size(); i++)
			((QuestionDef)questions.elementAt(i)).updateDataNodes(parentDataNode);
	}

	public void setMaxRows(byte maxRows) {
		this.maxRows = maxRows;
	}

	public byte getMaxRows() {
		return maxRows;
	}

	public void buildLanguageNodes(String parentXpath,com.google.gwt.xml.client.Document doc, Element parentXformNode, Element parentLangNode, Map<String, String> changedXpaths){
		if(questions == null)
			return;

		for(int i=0; i<questions.size(); i++)
			((QuestionDef)questions.elementAt(i)).buildLanguageNodes(parentXpath,doc,parentXformNode,parentLangNode, changedXpaths);
	}
}