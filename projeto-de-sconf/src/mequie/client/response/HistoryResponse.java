package mequie.client.response;

import java.util.ArrayList;
import java.util.List;

import mequie.server.domain.Texto;

public class HistoryResponse {
	private boolean error = false;
	private List<Texto> history = new ArrayList<>();
	
	public HistoryResponse() {
		
	}

	public HistoryResponse(boolean error) {
		super();
		this.error = error;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public List<Texto> getHistory() {
		return history;
	}

	public void setHistory(List<Texto> history) {
		this.history = history;
	}
	
	public void addTexto(Texto t) {
		this.history.add(t);
	}
	
	
}
