package jp.jamsketch.web;

class ClientParameter {
    public int from;
    public int thru;
	public int y;

    public ClientParameter(){
        
    }

    public ClientParameter(int from, int thru, int y){
        this.from = from;
        this.thru = thru;
        this.y = y;
    }
}