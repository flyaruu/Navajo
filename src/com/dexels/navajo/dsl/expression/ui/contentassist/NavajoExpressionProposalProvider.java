/*
* generated by Xtext
*/
package com.dexels.navajo.dsl.expression.ui.contentassist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

import com.dexels.navajo.dsl.expression.proposals.FunctionProposal;
import com.dexels.navajo.dsl.expression.proposals.INavajoContextProvider;
import com.dexels.navajo.dsl.expression.proposals.InputTmlProposal;
import com.dexels.navajo.dsl.expression.proposals.NavajoContextProvider;
import com.dexels.navajo.dsl.expression.proposals.NavajoResourceFinder;
import com.dexels.navajo.dsl.expression.proposals.TestNavajoResourceFinder;
import com.dexels.navajo.dsl.model.expression.Expression;
import com.dexels.navajo.dsl.model.tsl.ExpressionTag;
import com.dexels.navajo.dsl.model.tsl.Map;
import com.google.inject.Inject;
/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class NavajoExpressionProposalProvider extends AbstractNavajoExpressionProposalProvider {

	// TODO Rewrite to do dep inj.
	@Inject
	protected  INavajoContextProvider navajoContext; // = new NavajoContextProvider();
	
	@Inject
	public NavajoExpressionProposalProvider() {
	
	}	


	public void complete_FunctionCall(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
		super.complete_FunctionCall(model, ruleCall, context, acceptor);
		// compute the plain proposal
		for (FunctionProposal f : navajoContext.getFunctions()) {
			ICompletionProposal completionProposal = createCompletionProposal(f.getProposal(true), f.getProposalDescription(), null, context);
			acceptor.accept(completionProposal);
		}
		// convert it to a valid STRING-terminal
		// proposal = getValueConverter().toString(proposal, "ID");
		// create the completion proposal // the result may be null as the
		// createCompletionProposal(..) methods // check for valid prefixes //
		// and terminal token conflicts
	}

//	@Override
//	public void completeFunctionOperands_Operands(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
//		System.err.println("CLASSSS: "+model.getClass());
//		Expression e = (Expression) model;
//		String functionName = e.getOperations().get(0);
//		List<FunctionProposal> fd = findFunctionByName(functionName);
//		System.err.println("FD count: "+fd.size());
//		for (FunctionProposal f : fd) {
//			System.err.println("name: "+f.getName()+" op: "+f.getOperandProposal(true));
//			ICompletionProposal completionProposal = createCompletionProposal(f.getOperandProposal(true), f.getProposalDescription(), null, context);
//			acceptor.accept(completionProposal);
//		}
//	}
//	
//	public void complete_PathSequence(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
//		super.complete_PathSequence(model, ruleCall, context, acceptor);
//		System.err.println("Paaaaaaa");
//		ICompletionProposal completionProposal = createCompletionProposal("/Club/ClubIdentifier", "[ClubCode] /Club/ClubIdentifier: BBFW63X", null, context);
//		acceptor.accept(completionProposal);
//		completionProposal = createCompletionProposal("/Club/ClubName", "[Club name] /Club/ClubName: De Schoof", null, context);
//		acceptor.accept(completionProposal);
//	}
//	
	public void complete_TmlExpression(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		super.complete_TmlExpression(model, ruleCall, context, acceptor);
//		ICompletionProposal completionProposal = createCompletionProposal("[/Club/ClubIdentifier]", "[ClubCode] /Club/ClubIdentifier: BBFW63X", null, context);
//		acceptor.accept(completionProposal);
//		completionProposal = createCompletionProposal("[/Club/ClubName]", "[Club name] /Club/ClubName: De Schoof", null, context);
//		acceptor.accept(completionProposal);
		for (InputTmlProposal tt : navajoContext.getTmlProposal()) {
			ICompletionProposal completionProposal = createCompletionProposal(tt.getProposal(), tt.getProposalDescription(), null, context);
			acceptor.accept(completionProposal);
		}
//		System.err.println("Pooooooooo!");
	}

	@Override
	public void complete_PathElement(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		super.complete_PathElement(model, ruleCall, context, acceptor);
		System.err.println("Completing path element!");
//		navajoContext.debugExpression((Expression) model);
	}
	
	@Override
	public void complete_MapGetReference(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		System.err.println("Completing map reference!");
		System.err.println("Map Stack: "+getMapStack(model));
		Expression mfr = null;
		List<String> proposals = new ArrayList<String>();
		navajoContext.processGetters(getMapStack(model), proposals, new StringBuffer());
		
		if(model instanceof ExpressionTag) {
			ExpressionTag et = (ExpressionTag)model;
			mfr = et.getExpression().getToplevelExpression();
		}
		if(model instanceof Expression) {
			mfr = (Expression)model;
		}
//		navajoContext.debugExpression(mfr);
		
		if(mfr!=null) {
			EList<String> elts = mfr.getElements();
			for (String e : elts) {
				System.err.println("Path element: "+e);
			}

			
		}
		for (String prop : proposals) {
			ICompletionProposal completionProposal = createCompletionProposal(prop,prop, null, context);
			acceptor.accept(completionProposal);
		}
	}
	
	
	
	
	private List<String> getMapStack(EObject modelNode) {
		List<String> result = new ArrayList<String>();
		EObject current = modelNode;
		while(current!=null) {
			if(current instanceof Map) {
				Map m = (Map)current;
				System.err.println("Map tag: "+m.getMapName());
				result.add(m.getMapName());
			}
			current = current.eContainer();
		}
		return result;
	}
	

	
	@Override
	public void complete_ExistsTmlExpression(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
		super.complete_ExistsTmlExpression(model, ruleCall, context, acceptor);
		if(navajoContext.getTmlProposal()==null) {
			
		}
		//		ICompletionProposal completionProposal = createCompletionProposal("?[/Club/ClubIdentifier]", "[ClubCode] /Club/ClubIdentifier: BBFW63X", null, context);
//		acceptor.accept(completionProposal);
//		completionProposal = createCompletionProposal("?[/Club/ClubName]", "[Club name] /Club/ClubName: De Schoof", null, context);
//		acceptor.accept(completionProposal);
		for (InputTmlProposal tt : navajoContext.getTmlProposal()) {
			ICompletionProposal completionProposal = createCompletionProposal("?"+tt.getProposal(), "?"+tt.getProposalDescription(), null, context);
			acceptor.accept(completionProposal);
		}

//		System.err.println("Ezis");
//		ResourcesPlugin.get
	}
	
	
	 public  IProject getCurrentProject(){
		  IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		  IEditorInput input =  editor.getEditorInput();
		  IFile file = null;
		  if(input instanceof IFileEditorInput){
		   file = ((IFileEditorInput)input).getFile();
		  }
		  if(file==null) {
			  return null;
		  }
		  IProject project = file.getProject();
		  return project;

		 }

	
	@Override
	public void complete_FunctionName(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		String proposal = "BROAOAOAOPO";

		proposal = getValueConverter().toString(proposal, "ID");
		System.err.println("Propposing");
		ICompletionProposal completionProposal = createCompletionProposal(
				proposal, context);
		// register the proposal, the acceptor handles null-values gracefully
		acceptor.accept(completionProposal);
	}

}
