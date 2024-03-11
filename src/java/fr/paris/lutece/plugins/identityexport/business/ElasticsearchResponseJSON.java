package fr.paris.lutece.plugins.identityexport.business;

import java.util.List;

public class ElasticsearchResponseJSON {
	private int took;
    private boolean timedOut;
    private Shards shards;
    private Hits hits;
    private String _scroll_id;
    private String pit_id;
    
    public String get_scroll_id() {
		return _scroll_id;
	}

	public void set_scroll_id(String _scroll_id) {
		this._scroll_id = _scroll_id;
	}

	public int getTook() {
        return took;
    }

    public void setTook(int took) {
        this.took = took;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    public Shards getShards() {
        return shards;
    }

    public void setShards(Shards shards) {
        this.shards = shards;
    }

    public Hits getHits() {
        return hits;
    }

    public void setHits(Hits hits) {
        this.hits = hits;
    }

    public String getPit_id() {
		return pit_id;
	}

	public void setPit_id(String pit_id) {
		this.pit_id = pit_id;
	}

	public static class Shards {
        // Propriétés des shards si nécessaire
    }

    public static class Hits {
        private Total total;
        private List<Hit> hits;

        public Total getTotal() {
            return total;
        }

        public void setTotal(Total total) {
            this.total = total;
        }

        public List<Hit> getHits() {
            return hits;
        }

        public void setHits(List<Hit> hits) {
            this.hits = hits;
        }
    }

    public static class Total {
        private int value;
        private String relation;

    }

    public static class Hit {
        private String _index;
        private String _type;
        private String _id;
        private double _score;
        private Source _source;
        private String[] sort;
        
		public Source get_source() {
			return _source;
		}
		public void set_source(Source _source) {
			this._source = _source;
		}
		public double get_score() {
			return _score;
		}
		public void set_score(double _score) {
			this._score = _score;
		}
		public String get_id() {
			return _id;
		}
		public void set_id(String _id) {
			this._id = _id;
		}
		public String[] getSort() {
			return sort;
		}
		public void setSort(String[] sort) {
			this.sort = sort;
		}

    }

    public static class Source {
        private String customerId;
        private String connectionId;
        private Object attributes;
        
        
		public String getCustomerId() {
			return customerId;
		}
		public void setCustomerId(String uid) {
			this.customerId = uid;
		}
		public Object getAttributes() {
			return attributes;
		}
		public void setAttributes(Object attribute) {
			this.attributes = attribute;
		}
		public String getConnectionId() {
			return connectionId;
		}
		public void setConnectionId(String connectionId) {
			this.connectionId = connectionId;
		}

    }
 
}
