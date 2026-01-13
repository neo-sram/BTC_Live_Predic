from flask import Flask, render_template
import numpy as np

app = Flask(__name__)


@app.route('/')
def home():
    try:
        preds = np.load('data/predictions.npy').flatten()
    except FileNotFoundError:
        return "<h1>Predictions not found. Please run the notebook to generate predictions.npy</h1>"

    preds_list = preds.tolist()
    return render_template('index.html', preds_list=preds_list)


if __name__ == '__main__':
    app.run(debug=True)
