import 'package:flutter/material.dart';

class ResultScreen extends StatefulWidget {
  final int correctAnswers;
  final int totalQuestions;
  final int totalExperience; // Общий заработанный опыт

  const ResultScreen({
    Key? key,
    required this.correctAnswers,
    required this.totalQuestions,
    required this.totalExperience,
  }) : super(key: key);

  @override
  _ResultScreenState createState() => _ResultScreenState();
}

class _ResultScreenState extends State<ResultScreen> {
  bool _showCongratulationDialog = false;

  @override
  void initState() {
    super.initState();
    // Проверяем, нужно ли показывать поздравление
    if (widget.totalExperience > 0) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        setState(() {
          _showCongratulationDialog = true;
        });
      });
    }
  }

  void _showDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Поздравляем!'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text('Вы заработали ${widget.totalExperience} опыта!'),
              SizedBox(height: 20),
              // Убедитесь, что картинка добавлена в assets/images/ и pubspec.yaml
              Image.asset(
                'assets/images/congratulations.png',
                width: 100,
                height: 100,
                errorBuilder: (context, error, stackTrace) => Icon(
                  Icons.image_not_supported,
                ), // Показать иконку, если картинка не найдена
              ),
            ],
          ),
          actions: <Widget>[
            TextButton(
              child: Text('Отлично!'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_showCongratulationDialog) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        _showDialog();
        WidgetsBinding.instance.addPostFrameCallback((_) {
          setState(() {
            _showCongratulationDialog = false; // Сбрасываем флаг
          });
        });
      });
    }

    return Scaffold(
      appBar: AppBar(title: Text('Результаты теста')),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                'Тест завершен!',
                style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 20),
              Text('Вы ответили правильно на:', style: TextStyle(fontSize: 18)),
              Text(
                '${widget.correctAnswers} из ${widget.totalQuestions}',
                style: TextStyle(
                  fontSize: 36,
                  fontWeight: FontWeight.bold,
                  color: Colors.blue,
                ),
              ),
              SizedBox(height: 20),
              Text('Общий заработанный опыт:', style: TextStyle(fontSize: 18)),
              Text(
                '${widget.totalExperience}',
                style: TextStyle(
                  fontSize: 36,
                  fontWeight: FontWeight.bold,
                  color: Colors.orange,
                ),
              ),
              SizedBox(height: 40),
              ElevatedButton(
                onPressed: () {
                  Navigator.popUntil(context, (route) => route.isFirst);
                },
                child: Text('Вернуться на карту'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
