import streamlit as st
import requests

st.set_page_config(page_title="monitor-dashboard")
st.title("Dashboard")
st.subheader("Sepolia ETH monitor")

API_URL = "http://localhost:8080/api/stats"

def fetch_data():
    try:
        response = requests.get(API_URL)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"API connection error: {e}")
        return[]
    
if st.button('fetch data'):
    with st.spinner('fetching data...'):
        data = fetch_data()
                           
        if data:

            col1, col2 = st.columns(2)
            with col1:
                st.metric(label="avg gas", value=f"{data['avgGas']:.2f}")
            with col2:
                st.metric(label="blocks", value=data['totalBlocks'])
            with st.expander("raw data"):
                st.json(data)
        else:
            st.warning("no stats")
else:
    st.info("click to fetch data")

