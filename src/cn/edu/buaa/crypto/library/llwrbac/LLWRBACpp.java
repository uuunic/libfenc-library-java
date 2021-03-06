package cn.edu.buaa.crypto.library.llwrbac;

import cn.edu.buaa.crypto.library.llwrbac.LLWRBAC.LLWRBACType;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class LLWRBACpp {
	private final Pairing pairing;
	
	private final int max_depth;
	private final int max_role;
	
	private final Element g;
	private final Element g_1;
	private final Element g_2;
	private final Element g_3;
	private final Element[] u;
	
	private final LLWRBACType type;
	
	private final RoleManager roleManager;
	
	public LLWRBACpp(Pairing pairing, LLWRBACType type, int D, int N, Element alpha){
		this.pairing = pairing;
		this.type = type;
		this.max_depth = D;
		this.max_role = N;
		
		this.g = pairing.getG1().newRandomElement().getImmutable();
		this.g_1 = this.g.powZn(alpha).getImmutable();
		this.g_2 = pairing.getG1().newRandomElement().getImmutable();
		this.g_3 = pairing.getG1().newRandomElement().getImmutable();
		
		if (type == LLWRBACType.CPASecure){
			this.u = new Element[max_role];
			for (int i=0; i<max_role; i++){
				this.u[i] = pairing.getG1().newRandomElement().getImmutable();
			}
		}else{
			this.u = new Element[max_role+1];
			for (int i=0; i<max_role; i++){
				this.u[i] = pairing.getG1().newRandomElement().getImmutable();
			}
			this.u[max_role] = pairing.getG1().newRandomElement().getImmutable();
		}
		
		this.roleManager = new RoleManager(this, D, N);
	}
	
	public Element get_u(int index){
		return this.u[index].duplicate();
	}
	
	public int get_u_length(){
		return this.u.length;
	}
	
	public Element get_g(){
		return this.g.duplicate();
	}
	
	public Element get_g_1(){
		return this.g_1.duplicate();
	}
	
	public Element get_g_2(){
		return this.g_2.duplicate();
	}
	
	public Element get_g_3(){
		return this.g_3.duplicate();
	}
	
	public int get_max_depth(){
		return this.max_depth;
	}
	
	public int get_max_role(){
		return this.max_role;
	}
	
	public Pairing get_pairing(){
		return this.pairing;
	}
	
	public LLWRBACType getType(){
		return this.type;
	}
	
	public RoleManager get_role_manager(){
		return this.roleManager;
	}
}
