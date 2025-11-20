import axios from 'axios';
import React, { useState } from 'react';
import {
    Alert,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
    ScrollView,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Icon from 'react-native-vector-icons/Ionicons';

import AsyncStorage from '@react-native-async-storage/async-storage';

const API_BASE_URL = 'http://100.66.95.13:8080/api/v1';

const validatePassword = (password: string) => {
    const regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,16}$/;
    if (!password) return "비밀번호를 입력해주세요.";
    if (!regex.test(password)) return "비밀번호는 8~16자, 영문, 숫자, 특수문자를 포함해야 합니다.";
    return null;
};

const validateEmail = (email: string) => {
    const regex = /\S+@\S+\.\S+/;
    if (!email) return "이메일을 입력해주세요.";
    if (!regex.test(email)) return "유효한 이메일 형식이 아닙니다.";
    return null;
};

export default function SignInPage({ navigation }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState('');
    const [validationErrors, setValidationErrors] = useState({ email: '', password: '' });
    const [showPassword, setShowPassword] = useState(false);

    const validateForm = () => {
        const emailError = validateEmail(email);
        const passwordError = validatePassword(password);
        setValidationErrors({ email: emailError, password: passwordError });
        return !emailError && !passwordError;
    };

    const handleSubmit = async () => {
        if (!validateForm()) return;

        setIsLoading(true);
        setApiError('');

        try {
            const response = await axios.post(`${API_BASE_URL}/auth/login`, {
                email,
                password,
            });

            if (response.data.success) {
                const { access_token } = response.data.data;
                
                await AsyncStorage.setItem('userToken', access_token);
                
                navigation.replace('Tabs', {screen: 'Main'}); 

            } else {
                setApiError(response.data.message || "로그인에 실패했습니다.");
            }
        } catch (error) {
            console.error("Login API Error:", error);
            if (error.response && error.response.data && error.response.data.message) {
                setApiError(error.response.data.message);
            } else {
                setApiError("서버 연결에 실패했습니다. API 주소를 확인해 주세요.");
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <View style={styles.fullScreenBackground}>
            <SafeAreaView style={styles.fullScreen}>
                
                {/* Danchive 로고 */}
                <View style={styles.topLogoContainer}>
                    <Text style={styles.topLogoText}>Danchive</Text> 
                </View>

                <View style={styles.outerContainer}>
                    <ScrollView contentContainerStyle={styles.scrollContent}>
                        
                        <View style={styles.innerContainer}>
                            
                            {/* Login your account 타이틀 */}
                            <Text style={styles.loginTitle}>Login your account</Text> 
                            
                            {/* 이메일 Input */}
                            {/* styles.neumorphicStyle 적용 */}
                            <TextInput
                                style={[styles.input, styles.neumorphicStyle, validationErrors.email && styles.inputError]}
                                placeholder="Email"
                                value={email}
                                onChangeText={setEmail}
                                keyboardType="email-address"
                                autoCapitalize="none"
                                disabled={isLoading}
                            />
                            {validationErrors.email && <Text style={styles.errorText}>{validationErrors.email}</Text>}
                            
                            {/* 비밀번호 Input */}
                            {/* styles.neumorphicStyle 적용 */}
                            <View style={[styles.passwordContainer, styles.neumorphicStyle, validationErrors.password && styles.inputError]}>
                                <TextInput
                                    style={styles.passwordInput}
                                    placeholder="Password"
                                    value={password}
                                    onChangeText={setPassword}
                                    secureTextEntry={!showPassword}
                                    disabled={isLoading}
                                />
                                <TouchableOpacity style={styles.iconButton} onPress={() => setShowPassword(!showPassword)}>
                                    <Icon name={showPassword ? "eye-off-outline" : "eye-outline"} size={20} color="#888" />
                                </TouchableOpacity>
                            </View>
                            {validationErrors.password && <Text style={styles.errorText}>{validationErrors.password}</Text>}
                        
                            {apiError ? <Text style={styles.apiErrorText}>{apiError}</Text> : null}

                            {/* 로그인 버튼 */}
                            <TouchableOpacity
                                style={styles.loginButton}
                                onPress={handleSubmit}
                                disabled={isLoading}
                            >
                                <Text style={styles.loginButtonText}>
                                    {isLoading ? 'Signing in...' : 'Login'}
                                </Text>
                            </TouchableOpacity>

                            {/* 하단 링크들 */}
                            <View style={styles.bottomLinksContainer}>
                                <TouchableOpacity onPress={() => navigation.navigate('PasswordResetRequest')}>
                                    <Text style={styles.linkText}>Forgot your password?</Text>
                                </TouchableOpacity>
                            </View>
                            

                        </View>

                    </ScrollView>
                </View>
            </SafeAreaView>
        </View>
    );
}

const styles = StyleSheet.create({
    fullScreen: {
        flex: 1,
    },
    fullScreenBackground: {
        flex: 1,
        backgroundColor: '#FFFFFF', 
    },
    topLogoContainer: { 
        width: '100%',
        alignItems: 'center', 
        paddingTop: 50,
        marginBottom: 30,
    },
    topLogoText: {
        fontSize: 32, 
        fontWeight: '700',
        color: '#023560',
    },
    outerContainer: {
        flex: 1,
        justifyContent: 'flex-start',
        alignItems: 'center',
        paddingHorizontal: 20,
    },
    scrollContent: {
        flexGrow: 1,
        paddingTop: 50,
        paddingBottom: 50, 
        alignItems: 'center',
        width: '100%',
    },

    innerContainer: {
        paddingTop: 10,
        width: '100%',
        alignItems: 'center',
    },
    loginTitle: {
        fontSize: 16,
        fontWeight: '700',
        color: '#6A6A6A',
        marginBottom: 30, 
        textAlign: 'left',
        width: 298,
        alignSelf: 'center',
    },

    neumorphicStyle: {
        borderRadius: 7,
        backgroundColor: '#F0F0F3',
        
        shadowColor: "#0D2750", 
        shadowOffset: {
            width: 8,
            height: 10,
        },
        shadowOpacity: 0.16, 
        shadowRadius: 10, 
        
        elevation: 8,
    },

    input: {
        width: 298,
        height: 42,
        paddingHorizontal: 15,
        fontSize: 16,
        borderWidth: 0,
        marginBottom: 20, 
        alignSelf: 'center',

    },
    inputError: {
        borderWidth: 1,
        borderColor: '#DA0000',
        backgroundColor: '#FFADAD', 
    },
    passwordContainer: {
        flexDirection: 'row',
        alignItems: 'center',
        width: 298,
        height: 42,
        borderWidth: 0,
        marginBottom: 10,
        alignSelf: 'center',
    },
    passwordInput: {
        flex: 1,
        paddingHorizontal: 15,
        fontSize: 16,
    },
    iconButton: {
        padding: 10,
    },
    errorText: {
        color: '#d9534f',
        fontSize: 12,
        marginBottom: 10,
        width: 298,
        textAlign: 'left',
    },

    loginButton: {
        width: 298, 
        height: 42, 
        backgroundColor: '#023560', 
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: 60, 
        alignSelf: 'center',
        borderRadius: 7,
    },
    loginButtonText: {
        color: '#FFF',
        fontSize: 14,
        fontWeight: '600',
        textAlign: 'center'
    },
    apiErrorText: {
        color: '#d9534f',
        fontSize: 14,
        textAlign: 'center',
        marginBottom: 15,
    },
    bottomLinksContainer: {
        flexDirection: 'row',
        justifyContent: 'center',
        marginTop: 10, 
    },
    linkText: {
        color: '#023560', 
        textDecorationLine: 'none', 
        fontSize: 12,
        fontWeight: '500', 
    },
});