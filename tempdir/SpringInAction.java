@Service
public class SpringInAction implements ApplicationListener<ContextRefreshedEvent>{

	@Autowired
	private UserService userService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		// TODO Auto-generated method stub
		System.err.println("- - - - here i come - - - - ");
		
		//root application context 没有parent，他就是老大.
		if(arg0.getApplicationContext().getParent() == null){
			System.err.println(" - - - - -  - - - - - - -");
		}
		
		if(this.userService == null){
			System.err.println("user is null");
		}else{
			System.err.println("user is not null");
		}
	}

}
