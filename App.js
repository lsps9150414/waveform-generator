import React from "react";
import {
  StyleSheet,
  View,
  Text,
  Button,
  Keyboard,
  TouchableOpacity,
  Dimensions,
} from "react-native";

import StopSquareWaveScreen from './src/StopSquareWaveScreen';
import SendSquareWaveScreen from "./src/SendSquareWaveScreen";
import SendPulseSequenceScreen from "./src/SendPulseSequenceScreen";

const styles = StyleSheet.create({
  container: {
    paddingTop: 24,
    flex: 1,
    backgroundColor: "#ddd",
  },
  tabButtonGroup: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  tabButton: {
    flex: 1,
  },
});

export default class App extends React.Component {
  state = {
    focused: 'squareWave',
  };

  focusSquareWave = () => {
    this.setState({
      focused: 'squareWave',
    });
  };
  
  focusPulseSequence = () => {
    this.setState({
      focused: 'pulseSequence',
    });
  };

  render() {
    const { focused } = this.state;

    const isSquareWave = focused === 'squareWave';

    return (
      <View style={styles.container}>
        <View style={styles.tabButtonGroup}>
          <Button style={styles.tabButton} title="SquareWave" onPress={this.focusSquareWave} disabled={isSquareWave} />
          <Button style={styles.tabButton} title="PulseSequence" onPress={this.focusPulseSequence} disabled={!isSquareWave} />
        </View>
        <TouchableOpacity onPress={() => { Keyboard.dismiss(); }}>
          {isSquareWave && (<SendSquareWaveScreen />)}
          {!isSquareWave && (<SendPulseSequenceScreen />)}
        </TouchableOpacity>
        <StopSquareWaveScreen />
      </View>
    );
  }
}
