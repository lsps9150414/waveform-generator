import React from 'react';
import {
  StyleSheet,
  Text,
  View,
  Keyboard,
  TouchableOpacity,
} from 'react-native';

 import TabView from 'react-native-scrollable-tab-view';
 import SendSquareWaveScreen from './src/SendSquareWaveScreen';
 import StopSquareWaveScreen from './src/StopSquareWaveScreen';
 import SendPulseSequenceScreen from './src/SendPulseSequenceScreen';

const styles = StyleSheet.create({
  container: {
    paddingTop: 24,
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});

export default class App extends React.Component {
  state = {

  };

  render() {
    return (
      <View style={styles.container}>
         <Text>Android Test Run</Text>
         <TouchableOpacity onPress={() => { Keyboard.dismiss(); }}>
         <SendSquareWaveScreen tabLabel="Send Square Wave" />
         <SendPulseSequenceScreen tabLabel="Send Pulse Sequence" />
         <StopSquareWaveScreen tabLabel="Stop Square Wave" />
         </TouchableOpacity>
      </View>
    );
  }
}
