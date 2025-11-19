import React from 'react';
import {
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
    Image,
    ImageBackground,
    StatusBar
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

const BACKGROUND_IMAGE = require('../assets/landing-background.png'); 
const LOGO_IMAGE = require('../assets/Danchive_white.png');

export default function AuthScreen({ navigation }) {
    return (
        <ImageBackground 
            source={BACKGROUND_IMAGE} 
            style={styles.background}
            resizeMode="cover"
        >
            <StatusBar barStyle="light-content" />

            <SafeAreaView style={styles.safeArea}>
                
                <View style={styles.logoContainer}>
                    <Image 
                        source={LOGO_IMAGE} 
                        style={styles.logoImage}
                        resizeMode="contain"
                    />
                </View>

                <View style={styles.buttonContainer}>
                    {/* 로그인 버튼 */}
                    <TouchableOpacity 
                        style={styles.glassButton} 
                        onPress={() => navigation.navigate('SignIn')}
                    >
                        <Text style={styles.buttonText}>로그인</Text>
                    </TouchableOpacity>

                    {/* 회원가입 버튼 */}
                    <TouchableOpacity 
                        style={[styles.glassButton, styles.signupButtonMargin]} 
                        onPress={() => navigation.navigate('SignUp')}
                    >
                        <Text style={styles.buttonText}>회원가입</Text>
                    </TouchableOpacity>
                </View>

            </SafeAreaView>
        </ImageBackground>
    );
}

const styles = StyleSheet.create({
    background: {
        flex: 1,
        width: '100%',
        height: '100%',
    },
    safeArea: {
        flex: 1,
        justifyContent: 'space-between',
        paddingHorizontal: 30,
        paddingBottom: 50,
    },
    logoContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    logoImage: {
        width: 281,
        height: 77, 
    },
    
    buttonContainer: {
        width: '100%',
        alignItems: 'center',
        marginBottom: 30,
    },

    glassButton: {
        width: '100%',
        height: 42,
        borderRadius: 5,
        
        borderWidth: 1,
        borderColor: '#FFFFFF',
        
        backgroundColor: 'rgba(255, 255, 255, 0.15)',
        
        justifyContent: 'center',
        alignItems: 'center',
    },
    
    signupButtonMargin: {
        marginTop: 10,
    },

    buttonText: {
        color: '#FFFFFF',
        fontSize: 14,
        fontWeight: '600',
    }
});