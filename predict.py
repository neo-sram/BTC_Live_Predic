import pandas as pd
import numpy as np
from keras.models import load_model
from sklearn.preprocessing import MinMaxScaler

# Load and fit the scaler on training data
sc = MinMaxScaler(feature_range=(0,1))
df_train = pd.read_csv('data/market_data.csv', parse_dates=['Time'], index_col='Time')
train_set = df_train[:'2026'].iloc[:,1:2].values 
sc.fit(train_set)

model = load_model('model.h5')
df_new = pd.read_csv('logs/market_data.csv')
if 'Time' in df_new.columns:
    df_new = df_new.set_index('Time')
new_data = df_new['High'].values.reshape(-1,1)
new_data_scaled = sc.transform(new_data)
seq_length = 60
x_new = []
if len(new_data_scaled) >= seq_length:
    for i in range(seq_length, len(new_data_scaled)):
        x_new.append(new_data_scaled[i-seq_length:i, 0])
    x_new = np.array(x_new)
    x_new = np.reshape(x_new, (x_new.shape[0], x_new.shape[1], 1))
    pred_new = model.predict(x_new)
    pred_new = sc.inverse_transform(pred_new)
    print('Predictions on new data:')
    print(pred_new)
else:
    print(f'Not enough data for prediction, have {len(new_data_scaled)}, need at least {seq_length} points')