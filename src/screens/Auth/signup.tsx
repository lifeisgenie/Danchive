import { Picker } from '@react-native-picker/picker';
import axios from 'axios';
import React, { useState } from 'react';
import {
    Alert,
    ScrollView,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Icon from 'react-native-vector-icons/Ionicons';

const API_BASE_URL = 'http://100.66.95.13:8080/api/v1';

// 유효성 검사 함수들
const validatePassword = (password: string) => {
    const regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,16}$/;
    if (!password) return "비밀번호를 입력해주세요.";
    if (!regex.test(password)) return "비밀번호는 8~16자, 영문, 숫자, 특수문자를 포함해야 합니다.";
    return null;
};

const validateEmail = (email: string) => {
    const regex = /\S+@\S+\.\S+/;
    if (!email) return "유효한 이메일 형식이 아닙니다.";
    return null;
};

export default function SignUpPage({ navigation }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [studentId, setStudentId] = useState('');
    const [role, setRole] = useState('team');
    const [department, setDepartment] = useState('');
    
    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState('');
    const [validationErrors, setValidationErrors] = useState({
        email: '', password: '', name: '', department: ''
    });

    const [showPassword, setShowPassword] = useState(false);

    const validateForm = () => {
        const emailError = validateEmail(email);
        const passwordError = validatePassword(password);
        const nameError = !name ? "이름을 입력해주세요." : '';
        const departmentError = !department ? "소속/학과를 입력해주세요." : '';
        
        setValidationErrors({ email: emailError, password: passwordError, name: nameError, department: departmentError });
        return !emailError && !passwordError && !nameError && !departmentError;
    };

    const handleSubmit = async () => {
        if (!validateForm()) return;

        setIsLoading(true);
        setApiError('');

        try {
            const response = await axios.post(`${API_BASE_URL}/auth/register`, {
                email,
                password,
                name,
                role,
                department,
                // studentId, 
            });

            if (response.data.success) {
                Alert.alert("회원가입 성공", response.data.message || "로그인 페이지로 이동합니다.", [
                    { text: "확인", onPress: () => navigation.navigate('SignIn') }
                ]);
            } else {
                setApiError(response.data.message || "회원가입에 실패했습니다.");
            }
        } catch (error) {
            console.error("Register API Error:", error);
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
                <ScrollView contentContainerStyle={styles.scrollContent}>
                    
                    <View style={styles.innerContainer}>
                        
                        {/* 제목 */}
                        <View style={styles.titleContainer}>
                            <Text style={styles.title}>Create{'\n'}New account</Text>
                        </View>

                        {/* 이름 */}
                        <TextInput
                            style={[styles.input, styles.neumorphicStyle, validationErrors.name && styles.inputError]}
                            placeholder="이름"
                            placeholderTextColor="#A0A0A0" 
                            value={name}
                            onChangeText={setName}
                            disabled={isLoading}
                        />
                        {validationErrors.name && <Text style={styles.errorText}>{validationErrors.name}</Text>}

                        {/* 학번 */}
                        <TextInput
                            style={[styles.input, styles.inputMargin, styles.neumorphicStyle]}
                            placeholder="학번"
                            placeholderTextColor="#A0A0A0"
                            value={studentId}
                            onChangeText={setStudentId}
                            keyboardType="numeric"
                            disabled={isLoading}
                        />

                        {/* 소속/학과 */}
                        <TextInput
                            style={[styles.input, styles.inputMargin, styles.neumorphicStyle, validationErrors.department && styles.inputError]}
                            placeholder="소속 학부"
                            placeholderTextColor="#A0A0A0"
                            value={department}
                            onChangeText={setDepartment}
                            disabled={isLoading}
                        />
                        {validationErrors.department && <Text style={styles.errorText}>{validationErrors.department}</Text>}

                        {/* 이메일 */}
                        <TextInput
                            style={[styles.input, styles.inputMargin, styles.neumorphicStyle, validationErrors.email && styles.inputError]}
                            placeholder="Email"
                            placeholderTextColor="#A0A0A0"
                            value={email}
                            onChangeText={setEmail}
                            keyboardType="email-address"
                            autoCapitalize="none"
                            disabled={isLoading}
                        />
                        {validationErrors.email && <Text style={styles.errorText}>{validationErrors.email}</Text>}
                        
                        {/* 비밀번호 */}
                        <View style={[styles.passwordContainer, styles.inputMargin, styles.neumorphicStyle, validationErrors.password && styles.inputError]}>
                            <TextInput
                                style={styles.passwordInput}
                                placeholder="Password"
                                placeholderTextColor="#A0A0A0"
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

                        {/* Role 선택 */}
                        <Text style={styles.label}>Role (Team/Professor)</Text>
                        <View style={[styles.pickerWrapper, styles.neumorphicStyle]}>
                            <Picker
                                selectedValue={role}
                                onValueChange={(itemValue) => setRole(itemValue)}
                                enabled={!isLoading}
                                style={styles.picker}
                                itemStyle={styles.pickerItem}
                            >
                                <Picker.Item label="Team Member" value="team" style={{fontSize:14}}/>
                                <Picker.Item label="Professor" value="prof" style={{fontSize:14}}/>
                            </Picker>
                        </View>
                        
                        {apiError ? <Text style={styles.apiErrorText}>{apiError}</Text> : null}

                        {/* 회원가입 버튼 */}
                        <TouchableOpacity
                            style={styles.signupButton}
                            onPress={handleSubmit}
                            disabled={isLoading}
                        >
                            <Text style={styles.signupButtonText}>
                                {isLoading ? '...' : 'Login'} 
                            </Text>
                        </TouchableOpacity>

                    </View>
                </ScrollView>
            </SafeAreaView>
        </View>
    );
}

const styles = StyleSheet.create({
    fullScreen: {
        flex: 1,
        backgroundColor: 'white',
    },
    fullScreenBackground: {
        flex: 1,
        backgroundColor: 'white',
    },
    scrollContent: {
        flexGrow: 1,
        paddingBottom: 50, 
        paddingTop: 40,
    },
    innerContainer: {
        width: '100%',
        alignItems: 'center',
    },
    
    titleContainer: {
        width: 298,
        marginBottom: 30,
    },
    title: {
        fontSize: 32, 
        fontWeight: '700',
        color: '#002341',
        lineHeight: 40, 
        textAlign: 'left', 
    },

    neumorphicStyle: {
        borderRadius: 7,
        backgroundColor: '#F0F0F3',
        
        shadowColor: "#0D2750", 
        shadowOffset: {
            width: 6,
            height: 6,
        },
        shadowOpacity: 0.15, 
        shadowRadius: 6, 
        elevation: 6,
    },

    input: {
        width: 298,
        height: 42,
        paddingHorizontal: 15,
        fontSize: 14,
        borderWidth: 0,
        color: '#000000',
        alignSelf: 'center',
    },
    inputMargin: {
        marginTop: 25,
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
        alignSelf: 'center',
    },
    passwordInput: {
        flex: 1,
        paddingHorizontal: 15,
        fontSize: 14,
        color: '#000000',
    },
    iconButton: {
        padding: 10,
    },

    label: {
        width: 298,
        fontSize: 12,
        fontWeight: '600',
        color: '#969696',
        marginBottom: 8,
        marginTop: 20,
        textAlign: 'left',
    },

    pickerWrapper: {
        width: 298,
        height: 50,
        justifyContent: 'center',
        alignSelf: 'center',
        overflow: 'hidden',
    },
    picker: {
        width: '100%',
        height: 50,
    },
    pickerItem: {
        fontSize: 14,
        height: 42,
    },
    
    errorText: {
        width: 298,
        color: '#d9534f',
        fontSize: 12, 
        marginTop: 5,
        textAlign: 'left',
    },
    apiErrorText: {
        color: '#d9534f',
        fontSize: 14,
        textAlign: 'center',
        marginVertical: 15,
    },

    signupButton: {
        width: 298,
        height: 42,
        backgroundColor: '#023560',
        borderRadius: 7,
        justifyContent: 'center',
        alignItems: 'center',
        alignSelf: 'center',
        marginTop: 50,
    },
    buttonMargin: {
        marginTop: 30,
        marginBottom: 20,
    },
    signupButtonText: {
        color: '#FFF',
        fontSize: 15,
        fontWeight: '700',
    },

    signInLink: {
        marginTop: 10,
        alignItems: 'center',
    },
    signInLinkText: {
        color: '#004080',
        textDecorationLine: 'underline',
        fontSize: 13,
    },
});